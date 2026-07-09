package pe.edu.nova.java.libs.mapper.utils.exception;

/**
 * Excepción lanzada cuando ocurre un error durante la conversión de tipos.
 * <p>
 * Contiene el valor que causó el error, el tipo origen y el tipo destino
 * de la conversión fallida.
 * </p>
 *
 * @author mapper-utils
 * @since 1.0
 */
public class TypeConversionException extends MappingException {

    /** Valor que causó el error de conversión. */
    private final Object value;

    /** Tipo origen de la conversión fallida. */
    private final Class<?> fromType;

    /** Tipo destino de la conversión fallida. */
    private final Class<?> toType;

    /**
     * Crea una excepción de conversión de tipos.
     *
     * @param value    valor que causó el error
     * @param fromType tipo origen de la conversión
     * @param toType   tipo destino de la conversión
     * @param cause    causa original de la excepción
     */
    public TypeConversionException(Object value, Class<?> fromType, Class<?> toType, Throwable cause) {
        super(
            String.format("Error convirtiendo '%s' de %s a %s", value, fromType.getName(), toType.getName()),
            fromType,
            toType,
            cause
        );
        this.value = value;
        this.fromType = fromType;
        this.toType = toType;
    }

    /**
     * Retorna el valor que causó el error de conversión.
     *
     * @return valor que causó el error
     */
    public Object getValue() {
        return value;
    }

    /**
     * Retorna el tipo origen de la conversión fallida.
     *
     * @return tipo origen de la conversión
     */
    public Class<?> getFromType() {
        return fromType;
    }

    /**
     * Retorna el tipo destino de la conversión fallida.
     *
     * @return tipo destino de la conversión
     */
    public Class<?> getToType() {
        return toType;
    }
}
