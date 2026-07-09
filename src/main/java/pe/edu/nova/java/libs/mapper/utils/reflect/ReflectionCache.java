package pe.edu.nova.java.libs.mapper.utils.reflect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Caché singleton thread-safe de metadatos de reflexión.
 * <p>
 * Usa {@link ConcurrentHashMap} para acceso concurrente sin sincronización
 * explícita. Almacena los metadatos de campos ({@link FieldInfo}) por cada
 * clase procesada, evitando el costo de introspección repetida.
 * </p>
 *
 * @author mapper-utils
 * @since 1.0
 */
public final class ReflectionCache {

    /** Instancia única del caché. */
    private static final ReflectionCache INSTANCE = new ReflectionCache();

    /** Mapa concurrente de clase a lista de metadatos de campos. */
    private final ConcurrentHashMap<Class<?>, List<FieldInfo>> cache;

    /**
     * Constructor privado para garantizar el patrón singleton.
     */
    private ReflectionCache() {
        this.cache = new ConcurrentHashMap<>();
    }

    /**
     * Retorna la instancia única del caché de reflexión.
     *
     * @return instancia singleton de {@code ReflectionCache}
     */
    public static ReflectionCache getInstance() {
        return INSTANCE;
    }

    /**
     * Obtiene los metadatos de campos para una clase.
     * <p>
     * Si no están en caché, los calcula mediante introspección y los almacena.
     * </p>
     *
     * @param clazz clase a inspeccionar (no puede ser {@code null})
     * @return lista inmutable de {@link FieldInfo}
     */
    public List<FieldInfo> getFields(Class<?> clazz) {
        return cache.computeIfAbsent(clazz, this::introspect);
    }

    /**
     * Busca un campo específico por nombre en una clase.
     *
     * @param clazz     clase donde buscar
     * @param fieldName nombre del campo
     * @return {@link Optional} con {@link FieldInfo} si existe, vacío en caso contrario
     */
    public Optional<FieldInfo> getField(Class<?> clazz, String fieldName) {
        return getFields(clazz).stream()
                .filter(fi -> fi.name().equals(fieldName))
                .findFirst();
    }

    /**
     * Limpia toda la caché de metadatos.
     */
    public void clear() {
        cache.clear();
    }

    /**
     * Retorna la cantidad de clases almacenadas en caché.
     *
     * @return cantidad de clases en caché
     */
    public int size() {
        return cache.size();
    }

    /**
     * Realiza la introspección de una clase para obtener sus campos declarados.
     *
     * @param clazz clase a inspeccionar
     * @return lista inmutable de {@link FieldInfo}
     */
    private List<FieldInfo> introspect(Class<?> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        List<FieldInfo> fields = new ArrayList<>(declaredFields.length);
        for (Field field : declaredFields) {
            field.setAccessible(true);
            fields.add(new FieldInfo(field.getName(), field.getType(), field));
        }
        return Collections.unmodifiableList(fields);
    }
}
