package com.dron.lightconfig.conversion;

/**
 * Deserializer, needed for some crafty cases e.g to deserialize enums, as Java doesn't know how to deserialize abstract enum.
 *
 * @author Vasiliy Dronov
 */
public interface TypeDeserializer<T> {

    /**
     * Performs the deserialization of type using custom deserializer
     *
     * @param value string value
     * @return value of required type
     */
    T deserialize(String value);
}
