package pe.edu.nova.java.libs.mapper.utils.config;

import pe.edu.nova.java.libs.mapper.utils.exception.MappingConfigException;

/**
 * Define un mapeo explícito entre un campo origen y un campo destino.
 * <p>
 * Soporta notación de punto para campos anidados
 * (por ejemplo, {@code "direccion.ciudad"}).
 * </p>
 *
 * @param sourceField nombre del campo origen (no puede ser {@code null} ni vacío)
 * @param targetField nombre del campo destino (no puede ser {@code null} ni vacío)
 *
 * @author mapper-utils
 * @since 1.0
 */
public record FieldMapping(String sourceField, String targetField) {

    /**
     * Constructor compacto con validación de parámetros.
     *
     * @param sourceField nombre del campo origen
     * @param targetField nombre del campo destino
     * @throws MappingConfigException si {@code sourceField} o {@code targetField}
     *                                 es {@code null} o vacío
     */
    public FieldMapping {
        if (sourceField == null || sourceField.isBlank()) {
            throw new MappingConfigException(
                    "El campo origen (sourceField) es obligatorio en FieldMapping");
        }
        if (targetField == null || targetField.isBlank()) {
            throw new MappingConfigException(
                    "El campo destino (targetField) es obligatorio en FieldMapping");
        }
    }

    /**
     * Método de fábrica para crear un mapeo explícito de campos.
     *
     * @param sourceField nombre del campo origen (no puede ser {@code null} ni vacío)
     * @param targetField nombre del campo destino (no puede ser {@code null} ni vacío)
     * @return nueva instancia de {@code FieldMapping}
     * @throws MappingConfigException si {@code sourceField} o {@code targetField}
     *                                 es {@code null} o vacío
     */
    public static FieldMapping of(String sourceField, String targetField) {
        return new FieldMapping(sourceField, targetField);
    }
}
