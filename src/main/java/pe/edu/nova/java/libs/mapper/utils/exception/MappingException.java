package pe.edu.nova.java.libs.mapper.utils.exception;

/**
 * Excepción base de la librería de mapeo.
 * <p>
 * Todas las excepciones de mapper-utils extienden de esta clase.
 * Provee información contextual sobre los tipos origen y destino
 * involucrados en el error de mapeo.
 * </p>
 *
 * @author mapper-utils
 * @since 1.0
 */
public class MappingException extends RuntimeException {

    /** Tipo del objeto origen involucrado en el error. */
    private final Class<?> sourceType;

    /** Tipo del objeto destino involucrado en el error. */
    private final Class<?> targetType;

    /**
     * Crea una excepción de mapeo con un mensaje descriptivo.
     *
     * @param message mensaje descriptivo del error
     */
    public MappingException(String message) {
        super(message);
        this.sourceType = null;
        this.targetType = null;
    }

    /**
     * Crea una excepción de mapeo con un mensaje descriptivo y una causa.
     *
     * @param message mensaje descriptivo del error
     * @param cause   causa original de la excepción
     */
    public MappingException(String message, Throwable cause) {
        super(message, cause);
        this.sourceType = null;
        this.targetType = null;
    }

    /**
     * Crea una excepción de mapeo con mensaje, tipo origen y tipo destino.
     *
     * @param message    mensaje descriptivo del error
     * @param sourceType tipo del objeto origen
     * @param targetType tipo del objeto destino
     */
    public MappingException(String message, Class<?> sourceType, Class<?> targetType) {
        super(message);
        this.sourceType = sourceType;
        this.targetType = targetType;
    }

    /**
     * Crea una excepción de mapeo con mensaje, tipo origen, tipo destino y causa.
     *
     * @param message    mensaje descriptivo del error
     * @param sourceType tipo del objeto origen
     * @param targetType tipo del objeto destino
     * @param cause      causa original de la excepción
     */
    public MappingException(String message, Class<?> sourceType, Class<?> targetType, Throwable cause) {
        super(message, cause);
        this.sourceType = sourceType;
        this.targetType = targetType;
    }

    /**
     * Retorna el tipo del objeto origen involucrado en el error.
     *
     * @return tipo del objeto origen, o {@code null} si no aplica
     */
    public Class<?> getSourceType() {
        return sourceType;
    }

    /**
     * Retorna el tipo del objeto destino involucrado en el error.
     *
     * @return tipo del objeto destino, o {@code null} si no aplica
     */
    public Class<?> getTargetType() {
        return targetType;
    }
}
