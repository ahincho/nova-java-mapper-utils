package pe.edu.nova.java.libs.mapper.utils.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import pe.edu.nova.java.libs.mapper.utils.exception.TypeConversionException;

/**
 * Registro de conversores predeterminados para tipos comunes.
 * <p>
 * Provee conversores bidireccionales para los pares de tipos más frecuentes:
 * {@code String} ↔ {@code Integer}, {@code String} ↔ {@code Long},
 * {@code String} ↔ {@code Double}, {@code String} ↔ {@code Boolean},
 * {@code String} ↔ {@code LocalDate}, {@code String} ↔ {@code LocalDateTime}.
 * </p>
 * <p>
 * Los conversores se registran automáticamente cuando no se provee configuración
 * explícita. Cada conversor envuelve las excepciones en
 * {@link TypeConversionException} con mensaje descriptivo en español.
 * </p>
 *
 * @author mapper-utils
 * @since 1.0
 */
public final class DefaultConverters {

    /** Formato ISO para fechas: {@code yyyy-MM-dd}. */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    /** Formato ISO para fecha-hora: {@code yyyy-MM-dd'T'HH:mm:ss}. */
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /** Mapa inmutable con todos los conversores predeterminados. */
    private static final Map<TypeConverterKey, TypeConverter<?, ?>> DEFAULTS;

    /** Conversor de {@code String} a {@code Integer}. */
    static final TypeConverter<String, Integer> STRING_TO_INTEGER = source -> {
        try {
            return Integer.valueOf(source);
        } catch (NumberFormatException e) {
            throw new TypeConversionException(source, String.class, Integer.class, e);
        }
    };

    /** Conversor de {@code Integer} a {@code String}. */
    static final TypeConverter<Integer, String> INTEGER_TO_STRING = source -> source.toString();

    /** Conversor de {@code String} a {@code Long}. */
    static final TypeConverter<String, Long> STRING_TO_LONG = source -> {
        try {
            return Long.valueOf(source);
        } catch (NumberFormatException e) {
            throw new TypeConversionException(source, String.class, Long.class, e);
        }
    };

    /** Conversor de {@code Long} a {@code String}. */
    static final TypeConverter<Long, String> LONG_TO_STRING = source -> source.toString();

    /** Conversor de {@code String} a {@code Double}. */
    static final TypeConverter<String, Double> STRING_TO_DOUBLE = source -> {
        try {
            return Double.valueOf(source);
        } catch (NumberFormatException e) {
            throw new TypeConversionException(source, String.class, Double.class, e);
        }
    };

    /** Conversor de {@code Double} a {@code String}. */
    static final TypeConverter<Double, String> DOUBLE_TO_STRING = source -> source.toString();

    /** Conversor de {@code String} a {@code Boolean}. */
    static final TypeConverter<String, Boolean> STRING_TO_BOOLEAN = source -> {
        try {
            return Boolean.valueOf(source);
        } catch (Exception e) {
            throw new TypeConversionException(source, String.class, Boolean.class, e);
        }
    };

    /** Conversor de {@code Boolean} a {@code String}. */
    static final TypeConverter<Boolean, String> BOOLEAN_TO_STRING = source -> source.toString();

    /** Conversor de {@code String} a {@code LocalDate} con formato ISO ({@code yyyy-MM-dd}). */
    static final TypeConverter<String, LocalDate> STRING_TO_LOCAL_DATE = source -> {
        try {
            return LocalDate.parse(source, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new TypeConversionException(source, String.class, LocalDate.class, e);
        }
    };

    /** Conversor de {@code LocalDate} a {@code String} con formato ISO ({@code yyyy-MM-dd}). */
    static final TypeConverter<LocalDate, String> LOCAL_DATE_TO_STRING = source ->
            source.format(DATE_FORMATTER);

    /** Conversor de {@code String} a {@code LocalDateTime} con formato ISO ({@code yyyy-MM-dd'T'HH:mm:ss}). */
    static final TypeConverter<String, LocalDateTime> STRING_TO_LOCAL_DATE_TIME = source -> {
        try {
            return LocalDateTime.parse(source, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new TypeConversionException(source, String.class, LocalDateTime.class, e);
        }
    };

    /** Conversor de {@code LocalDateTime} a {@code String} con formato ISO ({@code yyyy-MM-dd'T'HH:mm:ss}). */
    static final TypeConverter<LocalDateTime, String> LOCAL_DATE_TIME_TO_STRING = source ->
            source.format(DATE_TIME_FORMATTER);

    static {
        Map<TypeConverterKey, TypeConverter<?, ?>> map = new HashMap<>();
        map.put(TypeConverterKey.of(String.class, Integer.class), STRING_TO_INTEGER);
        map.put(TypeConverterKey.of(Integer.class, String.class), INTEGER_TO_STRING);
        map.put(TypeConverterKey.of(String.class, Long.class), STRING_TO_LONG);
        map.put(TypeConverterKey.of(Long.class, String.class), LONG_TO_STRING);
        map.put(TypeConverterKey.of(String.class, Double.class), STRING_TO_DOUBLE);
        map.put(TypeConverterKey.of(Double.class, String.class), DOUBLE_TO_STRING);
        map.put(TypeConverterKey.of(String.class, Boolean.class), STRING_TO_BOOLEAN);
        map.put(TypeConverterKey.of(Boolean.class, String.class), BOOLEAN_TO_STRING);
        map.put(TypeConverterKey.of(String.class, LocalDate.class), STRING_TO_LOCAL_DATE);
        map.put(TypeConverterKey.of(LocalDate.class, String.class), LOCAL_DATE_TO_STRING);
        map.put(TypeConverterKey.of(String.class, LocalDateTime.class), STRING_TO_LOCAL_DATE_TIME);
        map.put(TypeConverterKey.of(LocalDateTime.class, String.class), LOCAL_DATE_TIME_TO_STRING);
        DEFAULTS = Collections.unmodifiableMap(map);
    }

    /**
     * Constructor privado para evitar instanciación.
     */
    private DefaultConverters() {
    }

    /**
     * Retorna un mapa inmutable con los conversores predeterminados.
     * <p>
     * Incluye conversores bidireccionales para:
     * {@code String} ↔ {@code Integer}, {@code String} ↔ {@code Long},
     * {@code String} ↔ {@code Double}, {@code String} ↔ {@code Boolean},
     * {@code String} ↔ {@code LocalDate}, {@code String} ↔ {@code LocalDateTime}.
     * </p>
     *
     * @return mapa inmutable de {@link TypeConverterKey} a {@link TypeConverter}
     */
    public static Map<TypeConverterKey, TypeConverter<?, ?>> getDefaults() {
        return DEFAULTS;
    }
}
