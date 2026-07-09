package pe.edu.nova.java.libs.mapper.utils.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pe.edu.nova.java.libs.mapper.utils.NullStrategy;
import pe.edu.nova.java.libs.mapper.utils.converter.TypeConverter;
import pe.edu.nova.java.libs.mapper.utils.converter.TypeConverterKey;
import pe.edu.nova.java.libs.mapper.utils.exception.MappingConfigException;

/**
 * Configuración inmutable para operaciones de mapeo.
 * <p>
 * Define las reglas de mapeo entre un tipo origen y un tipo destino,
 * incluyendo mapeos explícitos de campos, exclusiones, conversores de tipo,
 * estrategia de manejo de nulos y configuraciones para tipos anidados.
 * </p>
 * <p>
 * Las instancias se crean mediante el {@link Builder} y son inmutables
 * una vez construidas, garantizando thread-safety y reutilización segura.
 * </p>
 *
 * @author mapper-utils
 * @since 1.0
 */
public final class MappingConfig {

    /** Lista inmutable de mapeos explícitos de campos. */
    private final List<FieldMapping> fieldMappings;

    /** Conjunto inmutable de nombres de campos excluidos del mapeo. */
    private final Set<String> excludedFields;

    /** Mapa inmutable de conversores de tipo registrados. */
    private final Map<TypeConverterKey, TypeConverter<?, ?>> converters;

    /** Estrategia global de manejo de nulos. */
    private final NullStrategy globalNullStrategy;

    /** Mapa inmutable de estrategias de nulos por campo. */
    private final Map<String, NullStrategy> fieldNullStrategies;

    /** Mapa inmutable de configuraciones para tipos anidados. */
    private final Map<Class<?>, MappingConfig> nestedConfigs;

    /**
     * Constructor privado — solo accesible desde {@link Builder}.
     *
     * @param fieldMappings       lista de mapeos explícitos de campos
     * @param excludedFields      conjunto de nombres de campos excluidos
     * @param converters          mapa de conversores de tipo
     * @param globalNullStrategy  estrategia global de manejo de nulos
     * @param fieldNullStrategies mapa de estrategias de nulos por campo
     * @param nestedConfigs       mapa de configuraciones para tipos anidados
     */
    private MappingConfig(List<FieldMapping> fieldMappings,
                          Set<String> excludedFields,
                          Map<TypeConverterKey, TypeConverter<?, ?>> converters,
                          NullStrategy globalNullStrategy,
                          Map<String, NullStrategy> fieldNullStrategies,
                          Map<Class<?>, MappingConfig> nestedConfigs) {
        this.fieldMappings = Collections.unmodifiableList(new ArrayList<>(fieldMappings));
        this.excludedFields = Collections.unmodifiableSet(new HashSet<>(excludedFields));
        this.converters = Collections.unmodifiableMap(new HashMap<>(converters));
        this.globalNullStrategy = globalNullStrategy;
        this.fieldNullStrategies = Collections.unmodifiableMap(new HashMap<>(fieldNullStrategies));
        this.nestedConfigs = Collections.unmodifiableMap(new HashMap<>(nestedConfigs));
    }

    /**
     * Retorna la lista inmutable de mapeos explícitos de campos.
     *
     * @return lista inmutable de {@link FieldMapping}
     */
    public List<FieldMapping> fieldMappings() {
        return fieldMappings;
    }

    /**
     * Retorna el conjunto inmutable de nombres de campos excluidos del mapeo.
     *
     * @return conjunto inmutable de nombres de campos excluidos
     */
    public Set<String> excludedFields() {
        return excludedFields;
    }

    /**
     * Retorna el mapa inmutable de conversores de tipo registrados.
     *
     * @return mapa inmutable de {@link TypeConverterKey} a {@link TypeConverter}
     */
    public Map<TypeConverterKey, TypeConverter<?, ?>> converters() {
        return converters;
    }

    /**
     * Retorna la estrategia global de manejo de nulos.
     *
     * @return estrategia global de nulos
     */
    public NullStrategy globalNullStrategy() {
        return globalNullStrategy;
    }

    /**
     * Retorna el mapa inmutable de estrategias de nulos por campo.
     *
     * @return mapa inmutable de nombre de campo a {@link NullStrategy}
     */
    public Map<String, NullStrategy> fieldNullStrategies() {
        return fieldNullStrategies;
    }

    /**
     * Retorna el mapa inmutable de configuraciones para tipos anidados.
     *
     * @return mapa inmutable de clase a {@link MappingConfig}
     */
    public Map<Class<?>, MappingConfig> nestedConfigs() {
        return nestedConfigs;
    }

    /**
     * Retorna una configuración con valores predeterminados.
     * <p>
     * Mapeo por convención habilitado, {@link NullStrategy#MAP} como estrategia
     * global, sin exclusiones, sin conversores y sin configuraciones anidadas.
     * </p>
     *
     * @return configuración predeterminada
     */
    public static MappingConfig defaults() {
        return new MappingConfig(
                List.of(),
                Set.of(),
                Map.of(),
                NullStrategy.MAP,
                Map.of(),
                Map.of()
        );
    }

    /**
     * Genera una nueva {@code MappingConfig} con los mapeos invertidos.
     * <p>
     * El campo origen se convierte en campo destino y viceversa.
     * Las exclusiones se mantienen sin cambios.
     * </p>
     *
     * @return nueva configuración con mapeos invertidos
     */
    public MappingConfig reverse() {
        List<FieldMapping> reversedMappings = new ArrayList<>(fieldMappings.size());
        for (FieldMapping mapping : fieldMappings) {
            reversedMappings.add(FieldMapping.of(mapping.targetField(), mapping.sourceField()));
        }
        return new MappingConfig(
                reversedMappings,
                excludedFields,
                converters,
                globalNullStrategy,
                fieldNullStrategies,
                nestedConfigs
        );
    }

    /**
     * Crea un nuevo {@link Builder} para construir una instancia de {@code MappingConfig}.
     *
     * @return nuevo builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder fluido para construir instancias inmutables de {@link MappingConfig}.
     * <p>
     * Permite configurar mapeos de campos, exclusiones, conversores de tipo,
     * estrategia de nulos y configuraciones para tipos anidados.
     * </p>
     *
     * @author mapper-utils
     * @since 1.0
     */
    public static final class Builder {

        /** Lista de mapeos explícitos de campos. */
        private final List<FieldMapping> fieldMappings = new ArrayList<>();

        /** Conjunto de nombres de campos excluidos. */
        private final Set<String> excludedFields = new HashSet<>();

        /** Mapa de conversores de tipo. */
        private final Map<TypeConverterKey, TypeConverter<?, ?>> converters = new HashMap<>();

        /** Estrategia global de manejo de nulos (predeterminada: MAP). */
        private NullStrategy globalNullStrategy = NullStrategy.MAP;

        /** Mapa de estrategias de nulos por campo. */
        private final Map<String, NullStrategy> fieldNullStrategies = new HashMap<>();

        /** Mapa de configuraciones para tipos anidados. */
        private final Map<Class<?>, MappingConfig> nestedConfigs = new HashMap<>();

        /**
         * Constructor del builder.
         */
        Builder() {
        }

        /**
         * Agrega un mapeo explícito entre un campo origen y un campo destino.
         *
         * @param sourceField nombre del campo origen
         * @param targetField nombre del campo destino
         * @return este builder para encadenamiento fluido
         */
        public Builder fieldMapping(String sourceField, String targetField) {
            this.fieldMappings.add(FieldMapping.of(sourceField, targetField));
            return this;
        }

        /**
         * Agrega un mapeo explícito de campos.
         *
         * @param mapping mapeo explícito a agregar
         * @return este builder para encadenamiento fluido
         */
        public Builder fieldMapping(FieldMapping mapping) {
            this.fieldMappings.add(mapping);
            return this;
        }

        /**
         * Agrega una lista de mapeos explícitos de campos.
         *
         * @param mappings lista de mapeos explícitos a agregar
         * @return este builder para encadenamiento fluido
         */
        public Builder fieldMappings(List<FieldMapping> mappings) {
            this.fieldMappings.addAll(mappings);
            return this;
        }

        /**
         * Excluye un campo del mapeo.
         *
         * @param fieldName nombre del campo a excluir
         * @return este builder para encadenamiento fluido
         */
        public Builder exclude(String fieldName) {
            this.excludedFields.add(fieldName);
            return this;
        }

        /**
         * Excluye un conjunto de campos del mapeo.
         *
         * @param fieldNames conjunto de nombres de campos a excluir
         * @return este builder para encadenamiento fluido
         */
        public Builder excludeAll(Set<String> fieldNames) {
            this.excludedFields.addAll(fieldNames);
            return this;
        }

        /**
         * Registra un conversor de tipo para un par de tipos específicos.
         *
         * @param <S>        tipo origen de la conversión
         * @param <T>        tipo destino de la conversión
         * @param sourceType clase del tipo origen
         * @param targetType clase del tipo destino
         * @param converter  conversor de tipo a registrar
         * @return este builder para encadenamiento fluido
         */
        public <S, T> Builder converter(Class<S> sourceType, Class<T> targetType,
                                         TypeConverter<S, T> converter) {
            this.converters.put(TypeConverterKey.of(sourceType, targetType), converter);
            return this;
        }

        /**
         * Establece la estrategia global de manejo de nulos.
         *
         * @param strategy estrategia de nulos a aplicar globalmente
         * @return este builder para encadenamiento fluido
         */
        public Builder nullStrategy(NullStrategy strategy) {
            this.globalNullStrategy = strategy;
            return this;
        }

        /**
         * Establece una estrategia de nulos específica para un campo.
         *
         * @param fieldName nombre del campo
         * @param strategy  estrategia de nulos para el campo
         * @return este builder para encadenamiento fluido
         */
        public Builder fieldNullStrategy(String fieldName, NullStrategy strategy) {
            this.fieldNullStrategies.put(fieldName, strategy);
            return this;
        }

        /**
         * Registra una configuración de mapeo para un tipo anidado.
         *
         * @param nestedType tipo de la clase anidada
         * @param config     configuración de mapeo para el tipo anidado
         * @return este builder para encadenamiento fluido
         */
        public Builder nestedConfig(Class<?> nestedType, MappingConfig config) {
            this.nestedConfigs.put(nestedType, config);
            return this;
        }

        /**
         * Construye la instancia inmutable de {@link MappingConfig}.
         * <p>
         * Valida que no existan conflictos entre campos excluidos y mapeos
         * explícitos. Si un campo aparece tanto en exclusiones como en mapeos
         * explícitos, lanza {@link MappingConfigException}.
         * </p>
         *
         * @return nueva instancia inmutable de {@code MappingConfig}
         * @throws MappingConfigException si hay conflictos entre exclusiones y mapeos explícitos
         */
        public MappingConfig build() {
            // Validar conflictos entre exclusiones y mapeos explícitos
            for (FieldMapping mapping : fieldMappings) {
                if (excludedFields.contains(mapping.targetField())) {
                    throw new MappingConfigException(
                            String.format("Conflicto: el campo '%s' está excluido y también tiene un mapeo explícito",
                                    mapping.targetField()));
                }
                if (excludedFields.contains(mapping.sourceField())) {
                    throw new MappingConfigException(
                            String.format("Conflicto: el campo '%s' está excluido y también tiene un mapeo explícito",
                                    mapping.sourceField()));
                }
            }

            return new MappingConfig(
                    fieldMappings,
                    excludedFields,
                    converters,
                    globalNullStrategy,
                    fieldNullStrategies,
                    nestedConfigs
            );
        }
    }
}
