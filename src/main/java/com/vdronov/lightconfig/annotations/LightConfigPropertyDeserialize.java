package com.vdronov.lightconfig.annotations;

import com.vdronov.lightconfig.conversion.TypeDeserializer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to help light config to deserialize the specified property
 *
 * @author Vasiliy Dronov
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface LightConfigPropertyDeserialize {


    /**
     * @return custom deserializer
     */
    Class<? extends TypeDeserializer<?>> value();
}
