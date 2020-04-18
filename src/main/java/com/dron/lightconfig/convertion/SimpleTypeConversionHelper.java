package com.dron.lightconfig.convertion;

import com.dron.lightconfig.convertion.exceptions.ConversionException;
import com.dron.lightconfig.utils.MiscUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Vasiliy Dronov
 */
public class SimpleTypeConversionHelper {

    private final Map<Class<?>, TypeDeserializer<?>> deserializerMap;

    private SimpleTypeConversionHelper() {
        deserializerMap = new HashMap<>();

        deserializerMap.put(Integer.class, (TypeDeserializer<Integer>) Integer::new);
        deserializerMap.put(Float.class, (TypeDeserializer<Float>) Float::new);
        deserializerMap.put(Double.class, (TypeDeserializer<Double>) Double::new);
        deserializerMap.put(Short.class, (TypeDeserializer<Short>) Short::new);
        deserializerMap.put(Byte.class, (TypeDeserializer<Byte>) Byte::new);
        deserializerMap.put(Character.class, new CharDeserializer());
    }

    public static final SimpleTypeConversionHelper INSTANCE = new SimpleTypeConversionHelper();


    /**
     * Converts string value according to requested parameters
     *
     * @param value              string value
     * @param typeConversionInfo target type metadata
     * @return value bound to required type
     * @throws ConversionException if something goes wrong
     */
    @SuppressWarnings("rawtypes")
    public Object parseString(String value, TypeConversionInfo typeConversionInfo) {
        if (MiscUtils.isBlank(value)) {
            return null;
        }

        Class<?> returnType = typeConversionInfo.getReturnType();
        if (returnType.equals(String.class)) {
            return value;
        }

        try {
            TypeDeserializer TypeDeserializer = deserializerMap.get(returnType);

            if (TypeDeserializer != null) {
                return TypeDeserializer.deserialize(value);
            }

            if (Date.class.isAssignableFrom(returnType)) {
                if (MiscUtils.isBlank(typeConversionInfo.getFormat())) {
                    throw new ConversionException("Date format is not specified");
                }
                SimpleDateFormat format = new SimpleDateFormat(typeConversionInfo.getFormat());

                return format.parse(value);
            }

            Class<? extends com.dron.lightconfig.convertion.TypeDeserializer<?>> customDeserializer = typeConversionInfo.getCustomDeserializer();
            if (customDeserializer != null) {
                return customDeserializer.newInstance().deserialize(value);
            }
        } catch (Exception e) {
            throw new ConversionException(e);
        }
        throw new IllegalStateException("Can't parse type " + returnType + " from value " + value);

    }
}
