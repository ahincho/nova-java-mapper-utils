package pe.edu.nova.java.libs.mapper.utils.converter;

/**
 * Clave para búsqueda de {@link TypeConverter} en el registro de conversores.
 * <p>
 * Usa {@code equals}/{@code hashCode} automáticos del record para búsqueda
 * O(1) en {@code HashMap}. Identifica de forma única un par de tipos
 * (origen, destino) para el cual se registra un conversor.
 * </p>
 *
 * @param sourceType tipo origen de la conversión (no puede ser {@code null})
 * @param targetType tipo destino de la conversión (no puede ser {@code null})
 *
 * @author mapper-utils
 * @since 1.0
 */
public record TypeConverterKey(Class<?> sourceType, Class<?> targetType) {

    /**
     * Constructor compacto con validación de parámetros.
     *
     * @param sourceType tipo origen de la conversión
     * @param targetType tipo destino de la conversión
     * @throws IllegalArgumentException si {@code sourceType} o {@code targetType} es {@code null}
     */
    public TypeConverterKey {
        if (sourceType == null) {
            throw new IllegalArgumentException("El tipo origen (sourceType) es obligatorio");
        }
        if (targetType == null) {
            throw new IllegalArgumentException("El tipo destino (targetType) es obligatorio");
        }
    }

    /**
     * Método de fábrica para crear una clave de conversor de tipo.
     *
     * @param sourceType tipo origen de la conversión (no puede ser {@code null})
     * @param targetType tipo destino de la conversión (no puede ser {@code null})
     * @return nueva instancia de {@code TypeConverterKey}
     * @throws IllegalArgumentException si {@code sourceType} o {@code targetType} es {@code null}
     */
    public static TypeConverterKey of(Class<?> sourceType, Class<?> targetType) {
        return new TypeConverterKey(sourceType, targetType);
    }
}
