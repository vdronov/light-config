package com.dron.lightconfig.suppliers;

import java.util.Optional;

/**
 * Provider of raw properties
 *
 * @author Vasiliy Dronov
 */
public interface RawPropertiesSupplier {

    /**
     * Supplies the value for specified key only
     *
     * @param propertyName name of the propery
     * @return value for the specified property if any
     */
    Optional<String> supply(String propertyName);


}
