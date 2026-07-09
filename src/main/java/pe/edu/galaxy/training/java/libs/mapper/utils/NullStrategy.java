package pe.edu.nova.java.libs.mapper.utils;

/**
 * Define el comportamiento ante valores nulos durante el mapeo.
 * <p>
 * Permite configurar cómo el motor de mapeo maneja campos con valor
 * {@code null} en el objeto origen. Se puede establecer una estrategia
 * global y estrategias específicas por campo.
 * </p>
 *
 * @author mapper-utils
 * @since 1.0
 */
public enum NullStrategy {

    /**
     * Omitir el campo nulo, preservando el valor predeterminado del destino.
     * <p>
     * Cuando un campo origen es {@code null}, el campo destino mantiene
     * su valor actual sin ser modificado.
     * </p>
     */
    SKIP,

    /**
     * Asignar nulo al campo destino.
     * <p>
     * Cuando un campo origen es {@code null}, el campo destino se establece
     * explícitamente a {@code null}. Esta es la estrategia predeterminada.
     * </p>
     */
    MAP,

    /**
     * Lanzar {@link pe.edu.nova.java.libs.mapper.utils.exception.NullFieldException}
     * al encontrar un valor nulo.
     * <p>
     * Cuando un campo origen es {@code null}, se lanza una excepción
     * indicando el nombre del campo y el tipo del objeto origen.
     * </p>
     */
    THROW;

    /**
     * Constructor de la estrategia de manejo de nulos.
     */
    NullStrategy() {
    }
}
