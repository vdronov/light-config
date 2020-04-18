package com.dron.lightconfig.conversion;

import com.dron.lightconfig.utils.MiscUtils;

/**
 * Simple characters parser
 *
 * @author Vasiliy Dronov
 */
public class CharDeserializer implements TypeDeserializer<Character> {


    @Override
    public Character deserialize(String value) {
        if (MiscUtils.isBlank(value)) {
            return null;
        }
        return value.trim().toCharArray()[0];
    }
}
