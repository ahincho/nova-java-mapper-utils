package pe.edu.nova.java.libs.mapper.utils.converter;

/**
 * Interfaz funcional para conversión de tipos durante el mapeo.
 * <p>
 * Permite definir conversiones personalizadas entre un tipo origen y un tipo
 * destino. Al ser una interfaz funcional, soporta lambdas y method references.
 * </p>
 *
 * <pre>{@code
 * TypeConverter<String, Integer> converter = Integer::parseInt;
 * Integer resultado = converter.convert("42");
 * }</pre>
 *
 * @param <S> tipo origen de la conversión
 * @param <T> tipo destino de la conversión
 *
 * @author mapper-utils
 * @since 1.0
 */
@FunctionalInterface
public interface TypeConverter<S, T> {

    /**
     * Convierte un valor del tipo origen al tipo destino.
     *
     * @param source valor origen a convertir (nunca {@code null} — los nulos se manejan por NullStrategy)
     * @return valor convertido al tipo destino
     * @throws pe.edu.nova.java.libs.mapper.utils.exception.TypeConversionException
     *         si la conversión falla
     */
    T convert(S source);
}
