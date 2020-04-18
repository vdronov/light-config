package com.vdronov.lightconfig.conversion;

import com.vdronov.lightconfig.Color;

/**
 * @author Vasiliy Dronov
 */
public class ColorDeserializer implements TypeDeserializer<Color> {


    @Override
    public Color deserialize(String value) {
        return Color.valueOf(value.trim().toUpperCase());
    }
}
