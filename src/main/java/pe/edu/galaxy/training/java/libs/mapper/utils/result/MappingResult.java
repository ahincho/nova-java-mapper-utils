package pe.edu.nova.java.libs.mapper.utils.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Encapsula el resultado de una operación de mapeo con metadatos y advertencias.
 * <p>
 * Inmutable una vez construido. Contiene el objeto mapeado, la cantidad de
 * campos mapeados exitosamente, la cantidad de campos omitidos, una lista
 * de advertencias y un indicador de éxito.
 * </p>
 *
 * @param <T> tipo del objeto mapeado
 *
 * @author mapper-utils
 * @since 1.0
 */
public final class MappingResult<T> {

    /** Objeto mapeado resultante. */
    private final T value;

    /** Cantidad de campos mapeados exitosamente. */
    private final int mappedFieldCount;

    /** Cantidad de campos omitidos durante el mapeo. */
    private final int skippedFieldCount;

    /** Lista inmutable de advertencias generadas durante el mapeo. */
    private final List<String> warnings;

    /** Indica si el mapeo se completó sin errores fatales. */
    private final boolean success;

    /**
     * Constructor privado — solo accesible desde {@link Builder}.
     *
     * @param value            objeto mapeado resultante
     * @param mappedFieldCount cantidad de campos mapeados exitosamente
     * @param skippedFieldCount cantidad de campos omitidos
     * @param warnings         lista de advertencias
     * @param success          indicador de éxito
     */
    private MappingResult(T value, int mappedFieldCount, int skippedFieldCount,
                          List<String> warnings, boolean success) {
        this.value = value;
        this.mappedFieldCount = mappedFieldCount;
        this.skippedFieldCount = skippedFieldCount;
        this.warnings = Collections.unmodifiableList(new ArrayList<>(warnings));
        this.success = success;
    }

    /**
     * Retorna el objeto mapeado resultante.
     *
     * @return objeto mapeado, o {@code null} si el mapeo falló
     */
    public T getValue() {
        return value;
    }

    /**
     * Retorna la cantidad de campos mapeados exitosamente.
     *
     * @return cantidad de campos mapeados
     */
    public int getMappedFieldCount() {
        return mappedFieldCount;
    }

    /**
     * Retorna la cantidad de campos omitidos durante el mapeo.
     *
     * @return cantidad de campos omitidos
     */
    public int getSkippedFieldCount() {
        return skippedFieldCount;
    }

    /**
     * Retorna la lista inmutable de advertencias generadas durante el mapeo.
     *
     * @return lista inmutable de advertencias
     */
    public List<String> getWarnings() {
        return warnings;
    }

    /**
     * Indica si el mapeo se completó sin errores fatales.
     *
     * @return {@code true} si el mapeo fue exitoso, {@code false} en caso contrario
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Crea un nuevo {@link Builder} para construir una instancia de {@code MappingResult}.
     *
     * @param <T> tipo del objeto mapeado
     * @return nuevo builder
     */
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    /**
     * Builder fluido para construir instancias inmutables de {@link MappingResult}.
     *
     * @param <T> tipo del objeto mapeado
     *
     * @author mapper-utils
     * @since 1.0
     */
    public static final class Builder<T> {

        /** Objeto mapeado resultante. */
        private T value;

        /** Cantidad de campos mapeados exitosamente. */
        private int mappedFieldCount;

        /** Cantidad de campos omitidos. */
        private int skippedFieldCount;

        /** Lista de advertencias. */
        private final List<String> warnings = new ArrayList<>();

        /** Indicador de éxito. */
        private boolean success = true;

        /**
         * Constructor del builder.
         */
        Builder() {
        }

        /**
         * Establece el objeto mapeado resultante.
         *
         * @param value objeto mapeado
         * @return este builder para encadenamiento fluido
         */
        public Builder<T> value(T value) {
            this.value = value;
            return this;
        }

        /**
         * Establece la cantidad de campos mapeados exitosamente.
         *
         * @param count cantidad de campos mapeados
         * @return este builder para encadenamiento fluido
         */
        public Builder<T> mappedFieldCount(int count) {
            this.mappedFieldCount = count;
            return this;
        }

        /**
         * Establece la cantidad de campos omitidos.
         *
         * @param count cantidad de campos omitidos
         * @return este builder para encadenamiento fluido
         */
        public Builder<T> skippedFieldCount(int count) {
            this.skippedFieldCount = count;
            return this;
        }

        /**
         * Agrega una advertencia al resultado.
         *
         * @param warning mensaje de advertencia
         * @return este builder para encadenamiento fluido
         */
        public Builder<T> warning(String warning) {
            this.warnings.add(warning);
            return this;
        }

        /**
         * Agrega una lista de advertencias al resultado.
         *
         * @param warnings lista de mensajes de advertencia
         * @return este builder para encadenamiento fluido
         */
        public Builder<T> warnings(List<String> warnings) {
            this.warnings.addAll(warnings);
            return this;
        }

        /**
         * Establece el indicador de éxito del mapeo.
         *
         * @param success {@code true} si el mapeo fue exitoso
         * @return este builder para encadenamiento fluido
         */
        public Builder<T> success(boolean success) {
            this.success = success;
            return this;
        }

        /**
         * Construye la instancia inmutable de {@link MappingResult}.
         *
         * @return nueva instancia inmutable de {@code MappingResult}
         */
        public MappingResult<T> build() {
            return new MappingResult<>(value, mappedFieldCount, skippedFieldCount,
                    warnings, success);
        }
    }
}
