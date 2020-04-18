package com.dron.lightconfig.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to help assign config classes
 *
 * @author Vasiliy Dronov
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface LightConfigProperty {

    /**
     * @return name of the property
     */
    String value();


}
