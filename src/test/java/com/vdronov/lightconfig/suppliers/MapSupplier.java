package com.vdronov.lightconfig.suppliers;

import java.util.Map;
import java.util.Optional;

/**
 * @author Vasiliy Dronov
 */
public class MapSupplier implements RawPropertiesSupplier {

    private final Map<String, String> map;

    public MapSupplier(Map<String, String> map) {
        this.map = map;
    }

    @Override
    public Optional<String> supply(String propertyName) {
        return Optional.ofNullable(map.get(propertyName));
    }
}
