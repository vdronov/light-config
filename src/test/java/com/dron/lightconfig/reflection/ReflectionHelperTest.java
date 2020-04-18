package com.dron.lightconfig.reflection;

import com.dron.lightconfig.Color;
import com.dron.lightconfig.annotations.LightConfigProperty;
import com.dron.lightconfig.annotations.LightConfigPropertyDeserialize;
import com.dron.lightconfig.annotations.LightConfigPropertyFormat;
import com.dron.lightconfig.conversion.ColorDeserializer;
import com.dron.lightconfig.convertion.TypeDeserializer;
import com.dron.lightconfig.utils.MiscUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Vasiliy Dronov
 */
@SuppressWarnings("unused")
public class ReflectionHelperTest {

    private ReflectionHelper uut = ReflectionHelper.INSTANCE;

    private static class ColorListDeserializer implements TypeDeserializer<List<Color>>{
        @Override
        public List<Color> deserialize(String value) {
            ColorDeserializer colorDeserializer = new ColorDeserializer();
            return Arrays.stream(value.trim().split(",")).map(
                    colorDeserializer::deserialize
            ).collect(Collectors.toList());
        }
    }


    public interface GoodAppConfig {

        String dbUrl();

        @LightConfigProperty("db.poooort")
        Integer getDbPort();

        Long serverId();

        @LightConfigPropertyDeserialize(ColorDeserializer.class)
        Color getDesktopColor();

        @LightConfigPropertyFormat(format = "dd/MM/yyyy")
        Date applicationUpdateDate();

        @LightConfigPropertyDeserialize(ColorListDeserializer.class)
        List<Color> colors();
    }

    @Test
    void testMetadataExtractionHappyPath() {

        Map<String, MethodInfo> methodInfoMap = uut.getDeclaredMethodsMetadata(GoodAppConfig.class.getDeclaredMethods());

        MethodInfo dbUrl = methodInfoMap.get("dbUrl");

        assertEquals("db.url", dbUrl.getPropertyName());

        assertEquals(String.class, dbUrl.getReturnType());

        assertTrue(MiscUtils.isBlank(dbUrl.getFormat()));

        assertNull(dbUrl.getCustomDeserializer());

        //-------------------------------------------------

        MethodInfo dbPort = methodInfoMap.get("getDbPort");

        assertEquals("db.poooort", dbPort.getPropertyName());

        assertEquals(Integer.class, dbPort.getReturnType());

        assertTrue(MiscUtils.isBlank(dbPort.getFormat()));

        assertNull(dbPort.getCustomDeserializer());

        //-------------------------------------------------

        MethodInfo serverId = methodInfoMap.get("serverId");

        assertEquals("server.id", serverId.getPropertyName());

        assertEquals(Long.class, serverId.getReturnType());

        assertTrue(MiscUtils.isBlank(serverId.getFormat()));

        assertNull(serverId.getCustomDeserializer());

        //-------------------------------------------------

        MethodInfo desktopColor = methodInfoMap.get("getDesktopColor");

        assertEquals("desktop.color", desktopColor.getPropertyName());

        assertEquals(Color.class, desktopColor.getReturnType());

        assertTrue(MiscUtils.isBlank(desktopColor.getFormat()));

        assertEquals(ColorDeserializer.class, desktopColor.getCustomDeserializer());

        //-------------------------------------------------

        MethodInfo updatingDate = methodInfoMap.get("applicationUpdateDate");

        assertEquals("application.update.date", updatingDate.getPropertyName());

        assertEquals(Date.class, updatingDate.getReturnType());

        assertEquals("dd/MM/yyyy", updatingDate.getFormat());

        assertNull(updatingDate.getCustomDeserializer());

        //-------------------------------------------------

        MethodInfo colors = methodInfoMap.get("colors");

        assertEquals("colors", colors.getPropertyName());

        assertEquals(List.class, colors.getReturnType());

        assertTrue(MiscUtils.isBlank(colors.getFormat()));

        assertEquals(ColorListDeserializer.class, colors.getCustomDeserializer());

    }

    private interface BadAppConfig {
        Collection<Color> colors();
    }

    private interface BadAppConfigWithParameters {

        Long getServerId(Long id);

    }


    @Test
    void testBadAppConfigs() {
        assertThrows(IllegalArgumentException.class,
                () -> uut.getDeclaredMethodsMetadata(BadAppConfig.class.getDeclaredMethods())

        );

        assertThrows(IllegalArgumentException.class,
                () -> uut.getDeclaredMethodsMetadata(null)

        );

        assertThrows(IllegalArgumentException.class,
                () -> uut.getDeclaredMethodsMetadata(BadAppConfigWithParameters.class.getDeclaredMethods())

        );
    }
}
