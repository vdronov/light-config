package com.dron.lightconfig.reflection;

import com.dron.lightconfig.conversion.TypeConversionInfo;
import com.dron.lightconfig.conversion.TypeDeserializer;

/**
 * DTO to store the metadata of config's method
 *
 * @author Vasiliy Dronov
 */
public class MethodInfo implements TypeConversionInfo {

    /**
     * Name of the method
     */
    private String name;

    /**
     * Method's return type
     */
    private Class<?> returnType;

    /**
     * Property name
     */
    private String propertyName;

    /**
     * Format for dates
     */
    private String format;

    /**
     * Specific enum deserializer
     */
    private Class<? extends TypeDeserializer<?>> customDeserializer;


    @Override
    public Class<?> getReturnType() {
        return returnType;
    }

    /**
     * Sets new Method's return type.
     *
     * @param returnType New value of Method's return type.
     */
    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    /**
     * Sets new Name of the method.
     *
     * @param name New value of Name of the method.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets Name of the method.
     *
     * @return Value of Name of the method.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets new Property name.
     *
     * @param propertyName New value of Property name.
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * Gets Property name.
     *
     * @return Value of Property name.
     */
    public String getPropertyName() {
        return propertyName;
    }


    @Override
    public String getFormat() {
        return format;
    }

    /**
     * Sets new Format for dates.
     *
     * @param format New value of Format for dates.
     */
    public void setFormat(String format) {
        this.format = format;
    }


    /**
     * Sets new Specific enum deserializer.
     *
     * @param customDeserializer New value of Specific enum deserializer.
     */
    public void setCustomDeserializer(Class<? extends TypeDeserializer<?>> customDeserializer) {
        this.customDeserializer = customDeserializer;
    }

    @Override
    public Class<? extends TypeDeserializer<?>> getCustomDeserializer() {
        return customDeserializer;
    }
}
