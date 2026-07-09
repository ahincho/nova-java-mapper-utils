package pe.edu.nova.java.libs.mapper.utils.exception;

/**
 * Excepción lanzada cuando un campo referenciado no existe en el objeto origen o destino.
 * <p>
 * Contiene el nombre del campo y el tipo del objeto donde se buscó.
 * </p>
 *
 * @author mapper-utils
 * @since 1.0
 */
public class FieldNotFoundException extends MappingException {

    /** Nombre del campo que no fue encontrado. */
    private final String fieldName;

    /**
     * Crea una excepción indicando que un campo no fue encontrado.
     *
     * @param fieldName  nombre del campo no encontrado
     * @param objectType tipo del objeto donde se buscó el campo
     */
    public FieldNotFoundException(String fieldName, Class<?> objectType) {
        super(
            String.format("Campo '%s' no encontrado en tipo %s", fieldName, objectType.getName()),
            objectType,
            null
        );
        this.fieldName = fieldName;
    }

    /**
     * Retorna el nombre del campo que no fue encontrado.
     *
     * @return nombre del campo no encontrado
     */
    public String getFieldName() {
        return fieldName;
    }
}
