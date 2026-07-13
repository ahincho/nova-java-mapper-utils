plugins {
    id("java-library")
    id("maven-publish")
    id("info.solidsoft.pitest") version "1.19.0-rc.1"
    jacoco
    checkstyle
    id("net.nemerosa.versioning") version "4.0.1"
    id("signing")
    id("org.owasp.dependencycheck") version "12.2.2"
    id("org.cyclonedx.bom") version "3.2.4"
}

versioning {
    releaseMode = "snapshot"
    displayMode = "snapshot"
    releaseBuild = false
}

group = "pe.edu.nova.java.libs"
version = findProperty("version") as String

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenCentral()
}

val junitVersion = "6.0.0"
val jqwikVersion = "1.9.3"

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("org.junit.platform:junit-platform-launcher:$junitVersion")
    testImplementation("net.jqwik:jqwik:$jqwikVersion")
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        xml.outputLocation.set(
            layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml")
        )
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.javadoc {
    (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:all", "-quiet")
}

checkstyle {
    // Only lint production code. Test suites commonly rely on static-import
    // wildcards (org.junit.jupiter.api.Assertions.*, net.jqwik.api.*), which
    // is an accepted convention that would otherwise trip AvoidStarImport.
    sourceSets = listOf(project.sourceSets.main.get())
}

dependencyCheck {
    // NVD_API_KEY / NOVA_OWASP_FAIL_ON_CVSS are injected by reusable-owasp-check.yml.
    // Locally (no env vars set) this defaults to "never fail" (11.0, matches plugin default)
    // and an empty NVD key (slower updates, acceptable for local dev).
    failBuildOnCVSS = (System.getenv("NOVA_OWASP_FAIL_ON_CVSS") ?: "11").toFloat()
    nvd.apiKey = System.getenv("NVD_API_KEY") ?: ""

    // Must match the path reusable-owasp-check.yml caches AND restores the
    // shared nova-devops NVD mirror into. Do NOT rely on the plugin's
    // built-in default here - it was never verified/documented and previous
    // cache sizes (15-57MB) strongly suggest it did not match what was
    // being cached. Locally (no env var set) this falls back to a plain,
    // dedicated directory outside ~/.gradle so it is never confused with
    // unrelated Gradle caches.
    data.directory = System.getenv("NOVA_OWASP_DATA_DIR")
        ?: "${System.getProperty("user.home")}/.dependency-check-data"

    // Investigation (2026-07-13, docs/java/06-semantic-versioning-en-java.md):
    // a cold NVD sync took 50+ min mostly due to cache scoping, NOT these
    // analyzers - but disabling ecosystems that plainly do not exist
    // anywhere in this repo removes real (if smaller) analyze-phase
    // overhead and network surface at zero detection-feature cost.
    //
    // Deliberately NOT disabled: nodeEnabled / nodeAudit.enabled
    // (package.json IS present - commitlint/lefthook devDependencies -
    // keep scanning it for real) and opensslEnabled (harmless/fast).
    // RetireJS IS disabled: it fingerprints vendored/bundled JS *library*
    // files - this repo has no such files, only commitlint.config.js.
    analyzers {
        retirejs.enabled = false
        assemblyEnabled = false
        nuspecEnabled = false
        nugetconfEnabled = false
        msbuildEnabled = false
        golangDepEnabled = false
        golangModEnabled = false
        swiftEnabled = false
        swiftPackageResolvedEnabled = false
        cocoapodsEnabled = false
        composerEnabled = false
        cpanEnabled = false
        cmakeEnabled = false
        autoconfEnabled = false
        bundleAuditEnabled = false
        pyDistributionEnabled = false
        pyPackageEnabled = false
        rubygemsEnabled = false
        dartEnabled = false
    }
}

pitest {
    junit5PluginVersion.set("1.2.1")
    targetClasses.set(setOf("pe.edu.nova.java.libs.mapper.utils.*"))
    targetTests.set(setOf("pe.edu.nova.java.libs.mapper.utils.*"))
    mutators.set(setOf("DEFAULTS"))
    outputFormats.set(setOf("HTML", "XML"))
    pitestVersion.set("1.17.4")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/ahincho/nova-java-mapper-utils")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

signing {
    val gpgKeyId: String? = System.getenv("GPG_SIGNING_KEY_ID")
    val gpgKey: String? = System.getenv("GPG_SIGNING_KEY")
    val gpgPassword: String? = System.getenv("GPG_SIGNING_PASSWORD")

    if (gpgKeyId != null && gpgKey != null) {
        useInMemoryPgpKeys(gpgKeyId, gpgKey, gpgPassword ?: "")
        sign(publishing.publications)
    }
}