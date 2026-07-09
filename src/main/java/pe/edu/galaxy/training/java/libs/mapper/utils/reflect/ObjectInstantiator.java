package pe.edu.nova.java.libs.mapper.utils.reflect;

import java.lang.reflect.Constructor;

import pe.edu.nova.java.libs.mapper.utils.exception.ObjectInstantiationException;

/**
 * Clase utilitaria para crear instancias de tipos destino.
 * <p>
 * Soporta constructores privados mediante {@code setAccessible(true)}.
 * Utiliza el constructor sin argumentos (no-arg constructor) para
 * crear nuevas instancias.
 * </p>
 *
 * @author mapper-utils
 * @since 1.0
 */
public final class ObjectInstantiator {

    /**
     * Constructor privado para evitar instanciación.
     */
    private ObjectInstantiator() {
    }

    /**
     * Crea una nueva instancia del tipo especificado usando el constructor sin argumentos.
     * <p>
     * Soporta constructores privados mediante {@code setAccessible(true)}.
     * </p>
     *
     * @param <T>        tipo de la instancia a crear
     * @param targetType clase del tipo a instanciar (no puede ser {@code null})
     * @return nueva instancia del tipo especificado
     * @throws ObjectInstantiationException si no tiene constructor sin argumentos
     *                                       o falla la creación de la instancia
     */
    public static <T> T newInstance(Class<T> targetType) {
        try {
            Constructor<T> constructor = targetType.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new ObjectInstantiationException(targetType, e);
        }
    }
}
