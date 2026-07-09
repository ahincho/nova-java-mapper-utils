package pe.edu.nova.java.libs.mapper.utils.reflect;

import java.lang.reflect.Field;

import pe.edu.nova.java.libs.mapper.utils.exception.MappingException;

/**
 * Metadatos inmutables de un campo obtenidos por reflexión.
 * <p>
 * Encapsula el nombre, tipo y referencia al {@link Field} de reflexión
 * de un campo de clase. Provee métodos para leer y escribir valores
 * usando reflexión.
 * </p>
 *
 * @param name  nombre del campo (no puede ser {@code null} ni vacío)
 * @param type  tipo del campo (no puede ser {@code null})
 * @param field referencia al {@link Field} de reflexión (no puede ser {@code null})
 *
 * @author mapper-utils
 * @since 1.0
 */
public record FieldInfo(String name, Class<?> type, Field field) {

    /**
     * Constructor compacto con validación de parámetros.
     *
     * @param name  nombre del campo
     * @param type  tipo del campo
     * @param field referencia al {@link Field} de reflexión
     * @throws IllegalArgumentException si algún parámetro es nulo o el nombre es vacío
     */
    public FieldInfo {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre del campo es obligatorio");
        }
        if (type == null) {
            throw new IllegalArgumentException("El tipo del campo es obligatorio");
        }
        if (field == null) {
            throw new IllegalArgumentException("La referencia al Field es obligatoria");
        }
    }

    /**
     * Lee el valor del campo desde un objeto usando reflexión.
     *
     * @param source objeto del cual leer el valor
     * @return valor del campo en el objeto origen
     * @throws MappingException si ocurre un error al acceder al campo
     */
    public Object getValue(Object source) {
        try {
            return field.get(source);
        } catch (IllegalAccessException e) {
            throw new MappingException(
                String.format("Error al leer campo '%s' del tipo %s", name, source.getClass().getName()),
                source.getClass(),
                null,
                e
            );
        }
    }

    /**
     * Escribe un valor en el campo de un objeto usando reflexión.
     *
     * @param target objeto donde escribir el valor
     * @param value  valor a escribir en el campo
     * @throws MappingException si ocurre un error al escribir en el campo
     */
    public void setValue(Object target, Object value) {
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new MappingException(
                String.format("Error al escribir campo '%s' en tipo %s", name, target.getClass().getName()),
                null,
                target.getClass(),
                e
            );
        }
    }
}
