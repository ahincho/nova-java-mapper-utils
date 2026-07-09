package pe.edu.nova.java.libs.mapper.utils.exception;

/**
 * Excepción lanzada cuando no se puede crear una instancia del tipo destino.
 * <p>
 * Indica que el tipo destino no tiene un constructor sin argumentos accesible
 * o que la creación de la instancia falló por otra razón.
 * </p>
 *
 * @author mapper-utils
 * @since 1.0
 */
public class ObjectInstantiationException extends MappingException {

    /**
     * Crea una excepción de instanciación de objeto.
     *
     * @param targetType tipo del objeto que no se pudo instanciar
     * @param cause      causa original de la excepción
     */
    public ObjectInstantiationException(Class<?> targetType, Throwable cause) {
        super(
            String.format("No se puede crear instancia de %s: %s", targetType.getName(), cause.getMessage()),
            null,
            targetType,
            cause
        );
    }
}
