package com.dron.lightconfig.conversion;

import com.dron.lightconfig.Color;
import com.dron.lightconfig.convertion.TypeDeserializer;

/**
 * @author Vasiliy Dronov
 */
public class ColorDeserializer implements TypeDeserializer<Color> {


    @Override
    public Color deserialize(String value) {
        return Color.valueOf(value.trim().toUpperCase());
    }
}
