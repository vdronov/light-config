package com.dron.lightconfig.convertion;

/**
 * @author Vasiliy Dronov
 */
public interface TypeConversionInfo {
    /**
     * @return Method's return type.
     */
    Class<?> getReturnType();

    /**
     * @return Format for dates.
     */
    String getFormat();

    /**
     * @return custom class deserializer
     */
    Class<? extends TypeDeserializer<?>> getCustomDeserializer();
}
