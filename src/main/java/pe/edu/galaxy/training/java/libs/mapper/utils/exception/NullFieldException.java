package pe.edu.nova.java.libs.mapper.utils.exception;

/**
 * Excepción lanzada cuando se encuentra un campo nulo y la estrategia
 * de nulos es {@code THROW}.
 * <p>
 * Contiene el nombre del campo nulo y el tipo del objeto origen.
 * </p>
 *
 * @author mapper-utils
 * @since 1.0
 */
public class NullFieldException extends MappingException {

    /** Nombre del campo que contiene valor nulo. */
    private final String fieldName;

    /**
     * Crea una excepción indicando que un campo es nulo con estrategia THROW.
     *
     * @param fieldName  nombre del campo nulo
     * @param sourceType tipo del objeto origen donde se encontró el campo nulo
     */
    public NullFieldException(String fieldName, Class<?> sourceType) {
        super(
            String.format("Campo '%s' es nulo en tipo %s (NullStrategy=THROW)", fieldName, sourceType.getName()),
            sourceType,
            null
        );
        this.fieldName = fieldName;
    }

    /**
     * Retorna el nombre del campo que contiene valor nulo.
     *
     * @return nombre del campo nulo
     */
    public String getFieldName() {
        return fieldName;
    }
}
