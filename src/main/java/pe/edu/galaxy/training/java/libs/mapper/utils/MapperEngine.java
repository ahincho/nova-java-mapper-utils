package pe.edu.nova.java.libs.mapper.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import pe.edu.nova.java.libs.mapper.utils.config.MappingConfig;
import pe.edu.nova.java.libs.mapper.utils.engine.MappingExecutor;
import pe.edu.nova.java.libs.mapper.utils.exception.MappingException;
import pe.edu.nova.java.libs.mapper.utils.reflect.ObjectInstantiator;
import pe.edu.nova.java.libs.mapper.utils.result.MappingResult;

/**
 * Punto de entrada principal de la librería de mapeo.
 * <p>
 * Provee métodos estáticos para el caso de uso simple y un {@link Builder}
 * para configuración avanzada. Thread-safe — sin estado mutable compartido.
 * </p>
 *
 * <pre>{@code
 * // Mapeo simple por convención
 * TargetDto target = MapperEngine.map(source, TargetDto.class);
 *
 * // Mapeo con configuración
 * MappingConfig config = MappingConfig.builder()
 *     .fieldMapping("nombre", "name")
 *     .exclude("password")
 *     .build();
 * TargetDto target = MapperEngine.map(source, TargetDto.class, config);
 *
 * // Mapeo con Builder (retorna MappingResult)
 * MappingResult<TargetDto> result = MapperEngine.builder()
 *     .from(source)
 *     .to(TargetDto.class)
 *     .config(config)
 *     .execute();
 * }</pre>
 *
 * @author mapper-utils
 * @since 1.0
 */
public final class MapperEngine {

    /**
     * Constructor privado para evitar instanciación.
     */
    private MapperEngine() {
    }

    // --- Métodos Estáticos (caso simple — retorna T directamente) ---

    /**
     * Mapea un objeto origen al tipo destino usando convención de nombres.
     *
     * @param <T>        tipo del objeto destino
     * @param source     objeto origen (puede ser {@code null})
     * @param targetType clase del tipo destino (no puede ser {@code null})
     * @return instancia del tipo destino con campos mapeados, o {@code null} si source es {@code null}
     * @throws MappingException si {@code targetType} es {@code null}
     */
    public static <T> T map(Object source, Class<T> targetType) {
        return map(source, targetType, MappingConfig.defaults());
    }

    /**
     * Mapea un objeto origen al tipo destino usando configuración personalizada.
     *
     * @param <T>        tipo del objeto destino
     * @param source     objeto origen (puede ser {@code null})
     * @param targetType clase del tipo destino (no puede ser {@code null})
     * @param config     configuración de mapeo (no puede ser {@code null})
     * @return instancia del tipo destino con campos mapeados, o {@code null} si source es {@code null}
     * @throws MappingException si {@code targetType} o {@code config} es {@code null}
     */
    public static <T> T map(Object source, Class<T> targetType, MappingConfig config) {
        if (source == null) {
            return null;
        }
        validateTargetType(targetType);
        validateConfig(config);

        T target = ObjectInstantiator.newInstance(targetType);
        Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        MappingResult<T> result = MappingExecutor.execute(source, target, config, visited);
        return result.getValue();
    }

    // --- Métodos Estáticos para Colecciones ---

    /**
     * Mapea una lista de objetos al tipo destino usando convención de nombres.
     *
     * @param <T>        tipo del objeto destino
     * @param sourceList lista de objetos origen (puede ser {@code null})
     * @param targetType clase del tipo destino (no puede ser {@code null})
     * @return lista de instancias del tipo destino, o {@code null} si sourceList es {@code null}
     * @throws MappingException si {@code targetType} es {@code null}
     */
    public static <T> List<T> mapList(List<?> sourceList, Class<T> targetType) {
        return mapList(sourceList, targetType, MappingConfig.defaults());
    }

    /**
     * Mapea una lista de objetos al tipo destino usando configuración personalizada.
     *
     * @param <T>        tipo del objeto destino
     * @param sourceList lista de objetos origen (puede ser {@code null})
     * @param targetType clase del tipo destino (no puede ser {@code null})
     * @param config     configuración de mapeo (no puede ser {@code null})
     * @return lista de instancias del tipo destino, o {@code null} si sourceList es {@code null}
     * @throws MappingException si {@code targetType} o {@code config} es {@code null}
     */
    public static <T> List<T> mapList(List<?> sourceList, Class<T> targetType, MappingConfig config) {
        if (sourceList == null) {
            return null;
        }
        validateTargetType(targetType);
        validateConfig(config);

        if (sourceList.isEmpty()) {
            return List.of();
        }

        List<T> result = new ArrayList<>(sourceList.size());
        for (int i = 0; i < sourceList.size(); i++) {
            Object element = sourceList.get(i);
            try {
                result.add(map(element, targetType, config));
            } catch (Exception e) {
                throw new MappingException(
                        String.format("Error mapeando elemento en índice %d: %s", i, e.getMessage()),
                        e);
            }
        }
        return result;
    }

    /**
     * Mapea un conjunto de objetos al tipo destino usando convención de nombres.
     *
     * @param <T>        tipo del objeto destino
     * @param sourceSet  conjunto de objetos origen (puede ser {@code null})
     * @param targetType clase del tipo destino (no puede ser {@code null})
     * @return conjunto de instancias del tipo destino, o {@code null} si sourceSet es {@code null}
     * @throws MappingException si {@code targetType} es {@code null}
     */
    public static <T> Set<T> mapSet(Set<?> sourceSet, Class<T> targetType) {
        return mapSet(sourceSet, targetType, MappingConfig.defaults());
    }

    /**
     * Mapea un conjunto de objetos al tipo destino usando configuración personalizada.
     *
     * @param <T>        tipo del objeto destino
     * @param sourceSet  conjunto de objetos origen (puede ser {@code null})
     * @param targetType clase del tipo destino (no puede ser {@code null})
     * @param config     configuración de mapeo (no puede ser {@code null})
     * @return conjunto de instancias del tipo destino, o {@code null} si sourceSet es {@code null}
     * @throws MappingException si {@code targetType} o {@code config} es {@code null}
     */
    public static <T> Set<T> mapSet(Set<?> sourceSet, Class<T> targetType, MappingConfig config) {
        if (sourceSet == null) {
            return null;
        }
        validateTargetType(targetType);
        validateConfig(config);

        if (sourceSet.isEmpty()) {
            return Set.of();
        }

        Set<T> result = new HashSet<>();
        int index = 0;
        for (Object element : sourceSet) {
            try {
                result.add(map(element, targetType, config));
            } catch (Exception e) {
                throw new MappingException(
                        String.format("Error mapeando elemento en índice %d: %s", index, e.getMessage()),
                        e);
            }
            index++;
        }
        return result;
    }

    /**
     * Mapea una colección de objetos al tipo destino usando convención de nombres.
     *
     * @param <T>        tipo del objeto destino
     * @param source     colección de objetos origen (puede ser {@code null})
     * @param targetType clase del tipo destino (no puede ser {@code null})
     * @return colección de instancias del tipo destino, o {@code null} si source es {@code null}
     * @throws MappingException si {@code targetType} es {@code null}
     */
    public static <T> Collection<T> mapCollection(Collection<?> source, Class<T> targetType) {
        return mapCollection(source, targetType, MappingConfig.defaults());
    }

    /**
     * Mapea una colección de objetos al tipo destino usando configuración personalizada.
     *
     * @param <T>        tipo del objeto destino
     * @param source     colección de objetos origen (puede ser {@code null})
     * @param targetType clase del tipo destino (no puede ser {@code null})
     * @param config     configuración de mapeo (no puede ser {@code null})
     * @return colección de instancias del tipo destino, o {@code null} si source es {@code null}
     * @throws MappingException si {@code targetType} o {@code config} es {@code null}
     */
    public static <T> Collection<T> mapCollection(Collection<?> source, Class<T> targetType,
                                                    MappingConfig config) {
        if (source == null) {
            return null;
        }
        validateTargetType(targetType);
        validateConfig(config);

        if (source.isEmpty()) {
            return List.of();
        }

        List<T> result = new ArrayList<>(source.size());
        int index = 0;
        for (Object element : source) {
            try {
                result.add(map(element, targetType, config));
            } catch (Exception e) {
                throw new MappingException(
                        String.format("Error mapeando elemento en índice %d: %s", index, e.getMessage()),
                        e);
            }
            index++;
        }
        return result;
    }

    // --- Builder ---

    /**
     * Crea un nuevo {@link Builder} para configurar y ejecutar el mapeo.
     *
     * @return nuevo builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder fluido para configurar y ejecutar operaciones de mapeo avanzadas.
     * <p>
     * A diferencia de los métodos estáticos que retornan el objeto mapeado
     * directamente, el Builder retorna un {@link MappingResult} con metadatos
     * y advertencias.
     * </p>
     *
     * @author mapper-utils
     * @since 1.0
     */
    public static final class Builder {

        /** Objeto origen a mapear. */
        private Object source;

        /** Tipo destino del mapeo. */
        private Class<?> targetType;

        /** Instancia pre-construida del tipo destino. */
        private Object targetInstance;

        /** Configuración de mapeo. */
        private MappingConfig config;

        /**
         * Constructor del builder.
         */
        Builder() {
        }

        /**
         * Establece el objeto origen a mapear.
         *
         * @param source objeto origen
         * @return este builder para encadenamiento fluido
         */
        public Builder from(Object source) {
            this.source = source;
            return this;
        }

        /**
         * Establece el tipo destino del mapeo.
         *
         * @param <T>        tipo del objeto destino
         * @param targetType clase del tipo destino
         * @return este builder para encadenamiento fluido
         */
        public <T> Builder to(Class<T> targetType) {
            this.targetType = targetType;
            return this;
        }

        /**
         * Establece una instancia pre-construida del tipo destino.
         * <p>
         * Cuando se usa {@code into()}, el motor de mapeo escribe los valores
         * directamente en la instancia proporcionada en lugar de crear una nueva.
         * </p>
         *
         * @param <T>            tipo del objeto destino
         * @param targetInstance instancia pre-construida del tipo destino
         * @return este builder para encadenamiento fluido
         */
        public <T> Builder into(T targetInstance) {
            this.targetInstance = targetInstance;
            return this;
        }

        /**
         * Establece la configuración de mapeo.
         *
         * @param config configuración de mapeo
         * @return este builder para encadenamiento fluido
         */
        public Builder config(MappingConfig config) {
            this.config = config;
            return this;
        }

        /**
         * Ejecuta el mapeo y retorna {@link MappingResult} con metadatos.
         *
         * @param <T> tipo del objeto destino
         * @return {@link MappingResult} con objeto mapeado, conteo de campos y advertencias
         * @throws MappingException si la configuración es inválida o el mapeo falla
         */
        @SuppressWarnings("unchecked")
        public <T> MappingResult<T> execute() {
            if (source == null) {
                return MappingResult.<T>builder()
                        .value(null)
                        .mappedFieldCount(0)
                        .skippedFieldCount(0)
                        .success(true)
                        .build();
            }

            MappingConfig effectiveConfig = config != null ? config : MappingConfig.defaults();

            T target;
            if (targetInstance != null) {
                target = (T) targetInstance;
            } else {
                if (targetType == null) {
                    throw new MappingException("El tipo destino (targetType) es obligatorio");
                }
                target = (T) ObjectInstantiator.newInstance(targetType);
            }

            Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());
            return MappingExecutor.execute(source, target, effectiveConfig, visited);
        }
    }

    // --- Métodos de validación privados ---

    /**
     * Valida que el tipo destino no sea {@code null}.
     *
     * @param targetType tipo destino a validar
     * @throws MappingException si {@code targetType} es {@code null}
     */
    private static void validateTargetType(Class<?> targetType) {
        if (targetType == null) {
            throw new MappingException("El tipo destino (targetType) es obligatorio");
        }
    }

    /**
     * Valida que la configuración no sea {@code null}.
     *
     * @param config configuración a validar
     * @throws MappingException si {@code config} es {@code null}
     */
    private static void validateConfig(MappingConfig config) {
        if (config == null) {
            throw new MappingException("La configuración de mapeo (config) es obligatoria");
        }
    }
}
