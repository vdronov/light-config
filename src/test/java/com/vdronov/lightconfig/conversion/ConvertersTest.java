package com.vdronov.lightconfig.conversion;

import com.vdronov.lightconfig.Color;
import com.vdronov.lightconfig.conversion.exceptions.ConversionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Vasiliy Dronov
 */
public class ConvertersTest {

    private final SimpleTypeConversionHelper uut = SimpleTypeConversionHelper.INSTANCE;

    @Test
    void testSimpleConversions() {
        Assertions.assertEquals(1, uut.parseString("1", typeInfo(Integer.class)));

        Assertions.assertEquals((byte) 3, uut.parseString("3", typeInfo(Byte.class)));

        Assertions.assertEquals((short) 1, uut.parseString("1", typeInfo(Short.class)));

        Assertions.assertEquals(1.23F, uut.parseString("1.23", typeInfo(Float.class)));

        Assertions.assertEquals(1.4564654654, uut.parseString("1.4564654654", typeInfo(Double.class)));

        Assertions.assertEquals('C', uut.parseString("C", typeInfo(Character.class)));

        Assertions.assertEquals("avwwfwe", uut.parseString("avwwfwe", typeInfo(String.class)));
    }

    @Test
    void testDate() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Date date = format.parse("2020-09-04");

        String formattedDate = format.format(date);

        Assertions.assertEquals(date,
                uut.parseString(
                        formattedDate,
                        typeInfo(Date.class, format.toPattern(), null)
                )
        );

        Assertions.assertThrows(ConversionException.class, () ->
                uut.parseString(
                        formattedDate,
                        typeInfo(Date.class, null, null)
                )
        );

        Assertions.assertThrows(ConversionException.class, () ->
                uut.parseString(
                        formattedDate,
                        typeInfo(Date.class, null, null)
                )
        );
    }

    @Test
    void testEnum() {
        Assertions.assertEquals(Color.GREEN, uut.parseString("green", typeInfo(Color.class, null, ColorDeserializer.class)));
        Assertions.assertThrows(
                ConversionException.class,
                () -> uut.parseString("greenb", typeInfo(Color.class, null, ColorDeserializer.class))
        );
    }

    private TypeConversionInfo typeInfo(final Class<?> returnType) {
        return typeInfo(returnType, null, null);
    }

    private TypeConversionInfo typeInfo(final Class<?> returnType, final String dateFormat, final Class<? extends TypeDeserializer<?>> enumDeserializerClass) {
        return new TypeConversionInfo() {
            @Override
            public Class<?> getReturnType() {
                return returnType;
            }

            @Override
            public String getFormat() {
                return dateFormat;
            }

            @Override
            public Class<? extends TypeDeserializer<?>> getCustomDeserializer() {
                return enumDeserializerClass;
            }
        };
    }
}
