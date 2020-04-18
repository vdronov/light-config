package com.vdronov.lightconfig.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to help property representation format
 *
 * @author Vasiliy Dronov
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface LightConfigPropertyFormat {

    /**
     * @return format (e.g of date_, equals to yyyy-MM-dd'T'HH:mm:ss'Z' by default.
     */
    String format() default "yyyy-MM-dd'T'HH:mm:ss'Z'";

}
