package pe.edu.nova.java.libs.mapper.utils.engine;

import java.time.temporal.Temporal;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import pe.edu.nova.java.libs.mapper.utils.NullStrategy;
import pe.edu.nova.java.libs.mapper.utils.config.FieldMapping;
import pe.edu.nova.java.libs.mapper.utils.config.MappingConfig;
import pe.edu.nova.java.libs.mapper.utils.converter.DefaultConverters;
import pe.edu.nova.java.libs.mapper.utils.converter.TypeConverter;
import pe.edu.nova.java.libs.mapper.utils.converter.TypeConverterKey;
import pe.edu.nova.java.libs.mapper.utils.exception.FieldNotFoundException;
import pe.edu.nova.java.libs.mapper.utils.exception.MappingException;
import pe.edu.nova.java.libs.mapper.utils.exception.NullFieldException;
import pe.edu.nova.java.libs.mapper.utils.exception.TypeConversionException;
import pe.edu.nova.java.libs.mapper.utils.reflect.FieldInfo;
import pe.edu.nova.java.libs.mapper.utils.reflect.ObjectInstantiator;
import pe.edu.nova.java.libs.mapper.utils.reflect.ReflectionCache;
import pe.edu.nova.java.libs.mapper.utils.result.MappingResult;

/**
 * Motor interno que ejecuta la lógica de mapeo.
 * <p>
 * Maneja mapeo por convención, mapeo explícito, mapeo anidado recursivo
 * y detección de referencias circulares mediante {@code IdentityHashSet}.
 * </p>
 * <p>
 * Esta clase es package-private y no forma parte del API público.
 * </p>
 *
 * @author mapper-utils
 * @since 1.0
 */
public final class MappingExecutor {

    /** Mapa de equivalencia entre tipos primitivos y sus wrappers. */
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER = Map.ofEntries(
            Map.entry(boolean.class, Boolean.class),
            Map.entry(byte.class, Byte.class),
            Map.entry(char.class, Character.class),
            Map.entry(short.class, Short.class),
            Map.entry(int.class, Integer.class),
            Map.entry(long.class, Long.class),
            Map.entry(float.class, Float.class),
            Map.entry(double.class, Double.class)
    );

    /**
     * Constructor privado para evitar instanciación.
     */
    private MappingExecutor() {
    }

    /**
     * Ejecuta el mapeo completo de un objeto origen a un objeto destino.
     *
     * @param <T>     tipo del objeto destino
     * @param source  objeto origen
     * @param target  objeto destino (ya instanciado)
     * @param config  configuración de mapeo
     * @param visited set de identidad para detección de ciclos
     * @return {@link MappingResult} con el objeto mapeado y metadatos
     */
    public static <T> MappingResult<T> execute(Object source, T target,
                                                MappingConfig config,
                                                Set<Object> visited) {
        // Agregar source al set de visitados
        visited.add(source);

        MappingResult.Builder<T> resultBuilder = MappingResult.<T>builder()
                .value(target)
                .success(true);

        int mappedCount = 0;
        int skippedCount = 0;

        // Obtener campos del origen y destino
        ReflectionCache cache = ReflectionCache.getInstance();
        List<FieldInfo> sourceFields = cache.getFields(source.getClass());
        List<FieldInfo> targetFields = cache.getFields(target.getClass());

        // Rastrear campos destino ya procesados por mapeo explícito
        Set<String> processedTargetFields = new HashSet<>();

        // 1. Procesar mapeos explícitos primero
        for (FieldMapping mapping : config.fieldMappings()) {
            String sourceFieldName = mapping.sourceField();
            String targetFieldName = mapping.targetField();

            // Verificar exclusión
            if (config.excludedFields().contains(targetFieldName)) {
                skippedCount++;
                continue;
            }

            // Resolver valor del campo origen (soporta notación de punto)
            Object sourceValue;
            Class<?> sourceFieldType;
            if (sourceFieldName.contains(".")) {
                sourceValue = resolveNestedField(source, sourceFieldName);
                sourceFieldType = sourceValue != null ? sourceValue.getClass() : Object.class;
            } else {
                Optional<FieldInfo> sourceFieldOpt = cache.getField(source.getClass(), sourceFieldName);
                if (sourceFieldOpt.isEmpty()) {
                    throw new FieldNotFoundException(sourceFieldName, source.getClass());
                }
                FieldInfo sourceField = sourceFieldOpt.get();
                sourceValue = sourceField.getValue(source);
                sourceFieldType = sourceField.type();
            }

            // Buscar campo destino
            Optional<FieldInfo> targetFieldOpt = cache.getField(target.getClass(), targetFieldName);
            if (targetFieldOpt.isEmpty()) {
                throw new FieldNotFoundException(targetFieldName, target.getClass());
            }
            FieldInfo targetField = targetFieldOpt.get();

            // Manejar nulos
            if (sourceValue == null) {
                NullStrategy strategy = resolveNullStrategy(sourceFieldName, config);
                switch (strategy) {
                    case SKIP -> {
                        skippedCount++;
                        processedTargetFields.add(targetFieldName);
                        continue;
                    }
                    case THROW -> throw new NullFieldException(sourceFieldName, source.getClass());
                    case MAP -> {
                        if (!targetField.type().isPrimitive()) {
                            targetField.setValue(target, null);
                        }
                        mappedCount++;
                        processedTargetFields.add(targetFieldName);
                        continue;
                    }
                }
            }

            // Copiar valor con conversión si es necesario
            Object convertedValue = convertAndSet(sourceValue, sourceFieldType,
                    targetField, target, config, visited, resultBuilder);
            if (convertedValue != null || sourceValue == null) {
                mappedCount++;
            } else {
                skippedCount++;
                resultBuilder.warning(String.format(
                        "Tipos incompatibles para campo '%s' → '%s': %s → %s",
                        sourceFieldName, targetFieldName,
                        sourceFieldType.getName(), targetField.type().getName()));
            }
            processedTargetFields.add(targetFieldName);
        }

        // 2. Procesar mapeo por convención para campos restantes
        for (FieldInfo targetField : targetFields) {
            String fieldName = targetField.name();

            // Saltar campos ya procesados por mapeo explícito
            if (processedTargetFields.contains(fieldName)) {
                continue;
            }

            // Verificar exclusión
            if (config.excludedFields().contains(fieldName)) {
                skippedCount++;
                continue;
            }

            // Buscar campo con mismo nombre en origen
            Optional<FieldInfo> sourceFieldOpt = cache.getField(source.getClass(), fieldName);
            if (sourceFieldOpt.isEmpty()) {
                // Sin correspondencia — mantener valor predeterminado
                continue;
            }

            FieldInfo sourceField = sourceFieldOpt.get();
            Object sourceValue = sourceField.getValue(source);

            // Manejar nulos
            if (sourceValue == null) {
                NullStrategy strategy = resolveNullStrategy(fieldName, config);
                switch (strategy) {
                    case SKIP -> {
                        skippedCount++;
                        continue;
                    }
                    case THROW -> throw new NullFieldException(fieldName, source.getClass());
                    case MAP -> {
                        if (!targetField.type().isPrimitive()) {
                            targetField.setValue(target, null);
                        }
                        mappedCount++;
                        continue;
                    }
                }
            }

            // Copiar valor con conversión si es necesario
            Object convertedValue = convertAndSet(sourceValue, sourceField.type(),
                    targetField, target, config, visited, resultBuilder);
            if (convertedValue != null || sourceValue == null) {
                mappedCount++;
            } else {
                skippedCount++;
                resultBuilder.warning(String.format(
                        "Tipos incompatibles sin conversor para campo '%s': %s → %s",
                        fieldName, sourceField.type().getName(), targetField.type().getName()));
            }
        }

        return resultBuilder
                .mappedFieldCount(mappedCount)
                .skippedFieldCount(skippedCount)
                .build();
    }

    /**
     * Convierte y asigna un valor al campo destino.
     *
     * @param sourceValue   valor del campo origen
     * @param sourceType    tipo del campo origen
     * @param targetField   metadatos del campo destino
     * @param target        objeto destino
     * @param config        configuración de mapeo
     * @param visited       set de visitados para detección de ciclos
     * @param resultBuilder builder del resultado para agregar advertencias
     * @return valor convertido, o {@code null} si no se pudo convertir
     */
    private static Object convertAndSet(Object sourceValue, Class<?> sourceType,
                                         FieldInfo targetField, Object target,
                                         MappingConfig config, Set<Object> visited,
                                         MappingResult.Builder<?> resultBuilder) {
        Class<?> targetType = targetField.type();

        // Tipos iguales — copiar directamente
        if (sourceType.equals(targetType)) {
            targetField.setValue(target, sourceValue);
            return sourceValue;
        }

        // Tipos compatibles (primitivo/wrapper)
        if (isCompatibleType(sourceType, targetType)) {
            targetField.setValue(target, sourceValue);
            return sourceValue;
        }

        // Intentar conversión con TypeConverter
        Object converted = convertValue(sourceValue, sourceType, targetType, config);
        if (converted != null) {
            targetField.setValue(target, converted);
            return converted;
        }

        // Si es un tipo simple, no se puede convertir — omitir
        if (isSimpleType(sourceType) || isSimpleType(targetType)) {
            return null;
        }

        // Mapeo recursivo para objetos anidados no primitivos de tipo diferente
        if (!isPrimitiveOrWrapper(sourceType) && !isPrimitiveOrWrapper(targetType)) {
            // Verificar referencia circular
            if (visited.contains(sourceValue)) {
                targetField.setValue(target, null);
                resultBuilder.warning(String.format(
                        "Referencia circular detectada para tipo %s — se asignó null",
                        sourceType.getName()));
                return sourceValue; // Retornar no-null para contar como mapeado
            }

            try {
                // Obtener configuración anidada si existe
                MappingConfig nestedConfig = config.nestedConfigs().getOrDefault(targetType, config);
                Object nestedTarget = ObjectInstantiator.newInstance(targetType);
                Set<Object> nestedVisited = Collections.newSetFromMap(new IdentityHashMap<>());
                nestedVisited.addAll(visited);
                MappingResult<?> nestedResult = execute(sourceValue, nestedTarget, nestedConfig, nestedVisited);
                targetField.setValue(target, nestedResult.getValue());
                return nestedResult.getValue();
            } catch (Exception e) {
                resultBuilder.warning(String.format(
                        "Error en mapeo recursivo para campo '%s': %s",
                        targetField.name(), e.getMessage()));
                return null;
            }
        }

        return null;
    }

    /**
     * Resuelve un campo anidado usando notación de punto.
     * <p>
     * Divide la ruta por {@code "."} y navega campo por campo usando
     * {@link ReflectionCache}.
     * </p>
     *
     * @param source          objeto origen raíz
     * @param dotNotationPath ruta con notación de punto (por ejemplo, {@code "direccion.ciudad"})
     * @return valor del campo anidado
     * @throws FieldNotFoundException si algún campo intermedio no existe
     * @throws MappingException       si un campo intermedio es {@code null}
     */
    private static Object resolveNestedField(Object source, String dotNotationPath) {
        String[] parts = dotNotationPath.split("\\.");
        Object current = source;
        ReflectionCache cache = ReflectionCache.getInstance();

        for (int i = 0; i < parts.length; i++) {
            if (current == null) {
                return null;
            }
            String part = parts[i];
            Optional<FieldInfo> fieldOpt = cache.getField(current.getClass(), part);
            if (fieldOpt.isEmpty()) {
                throw new FieldNotFoundException(part, current.getClass());
            }
            current = fieldOpt.get().getValue(current);
        }
        return current;
    }

    /**
     * Intenta convertir un valor usando conversores registrados en la configuración
     * y luego los conversores predeterminados.
     *
     * @param value      valor a convertir
     * @param sourceType tipo origen
     * @param targetType tipo destino
     * @param config     configuración de mapeo
     * @return valor convertido, o {@code null} si no se encontró conversor
     */
    @SuppressWarnings("unchecked")
    private static Object convertValue(Object value, Class<?> sourceType,
                                        Class<?> targetType, MappingConfig config) {
        TypeConverterKey key = TypeConverterKey.of(sourceType, targetType);

        // Buscar en conversores de la configuración
        TypeConverter<Object, Object> converter =
                (TypeConverter<Object, Object>) config.converters().get(key);
        if (converter != null) {
            try {
                return converter.convert(value);
            } catch (TypeConversionException e) {
                throw e;
            } catch (Exception e) {
                throw new TypeConversionException(value, sourceType, targetType, e);
            }
        }

        // Buscar en conversores predeterminados
        converter = (TypeConverter<Object, Object>) DefaultConverters.getDefaults().get(key);
        if (converter != null) {
            try {
                return converter.convert(value);
            } catch (TypeConversionException e) {
                throw e;
            } catch (Exception e) {
                throw new TypeConversionException(value, sourceType, targetType, e);
            }
        }

        return null;
    }

    /**
     * Verifica si dos tipos son compatibles (equivalencia primitivo/wrapper).
     *
     * @param source tipo origen
     * @param target tipo destino
     * @return {@code true} si los tipos son compatibles
     */
    private static boolean isCompatibleType(Class<?> source, Class<?> target) {
        if (source.equals(target)) {
            return true;
        }
        Class<?> sourceWrapped = PRIMITIVE_TO_WRAPPER.getOrDefault(source, source);
        Class<?> targetWrapped = PRIMITIVE_TO_WRAPPER.getOrDefault(target, target);
        return sourceWrapped.equals(targetWrapped);
    }

    /**
     * Verifica si un tipo es primitivo o su wrapper correspondiente.
     *
     * @param type tipo a verificar
     * @return {@code true} si es primitivo o wrapper
     */
    private static boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive() || PRIMITIVE_TO_WRAPPER.containsValue(type);
    }

    /**
     * Verifica si un tipo es "simple" (no requiere mapeo recursivo).
     * <p>
     * Incluye: {@code String}, primitivos, wrappers, enums y tipos temporales.
     * </p>
     *
     * @param type tipo a verificar
     * @return {@code true} si es un tipo simple
     */
    private static boolean isSimpleType(Class<?> type) {
        return type.equals(String.class)
                || type.isPrimitive()
                || PRIMITIVE_TO_WRAPPER.containsValue(type)
                || type.isEnum()
                || Temporal.class.isAssignableFrom(type);
    }

    /**
     * Resuelve la estrategia de nulos para un campo específico.
     * <p>
     * Busca primero una estrategia por campo; si no existe, usa la global.
     * </p>
     *
     * @param fieldName nombre del campo
     * @param config    configuración de mapeo
     * @return estrategia de nulos aplicable
     */
    private static NullStrategy resolveNullStrategy(String fieldName, MappingConfig config) {
        NullStrategy fieldStrategy = config.fieldNullStrategies().get(fieldName);
        return fieldStrategy != null ? fieldStrategy : config.globalNullStrategy();
    }
}
