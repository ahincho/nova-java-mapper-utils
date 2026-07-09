package pe.edu.nova.java.libs.mapper.utils.exception;

/**
 * Excepción lanzada cuando hay un error en la configuración de mapeo.
 * <p>
 * Indica conflictos, campos inválidos u otros problemas en la
 * definición de {@code MappingConfig}.
 * </p>
 *
 * @author mapper-utils
 * @since 1.0
 */
public class MappingConfigException extends MappingException {

    /**
     * Crea una excepción de configuración de mapeo con un mensaje descriptivo.
     *
     * @param message mensaje descriptivo del error de configuración
     */
    public MappingConfigException(String message) {
        super(message);
    }
}
