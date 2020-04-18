package com.dron.lightconfig.utils;

import com.dron.lightconfig.utils.MiscUtils;
import com.dron.lightconfig.utils.StringParsingException;
import org.junit.jupiter.api.Test;

import static com.dron.lightconfig.utils.MiscUtils.extractPlaceholders;
import static com.dron.lightconfig.utils.MiscUtils.replaceCamelCaseWithDots;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Vasiliy Dronov
 */
public class MiscUtilsTest {

    @Test
    void camelCaseToDots() {
        String empty = "    ";
        assertEquals(empty, replaceCamelCaseWithDots(empty));

        assertNull(replaceCamelCaseWithDots(null));

        assertEquals("get.some.string.with.dots", replaceCamelCaseWithDots("getSomeStringWithDots"));

        assertEquals("get.some.string.with.dots", replaceCamelCaseWithDots("GetSomeStringWithDots"));

        assertEquals("some.string.with.dots", replaceCamelCaseWithDots("SomeStringWithDots"));

        assertEquals("s.d", replaceCamelCaseWithDots("sD"));

        assertThrows(StringParsingException.class, () -> replaceCamelCaseWithDots("getABC"));

        assertThrows(StringParsingException.class, () -> replaceCamelCaseWithDots("ABC"));
    }

    @Test
    void testExtractPlaceholders() {
        assertTrue(extractPlaceholders("", null, null).isEmpty());

        assertThrows(IllegalArgumentException.class, () -> extractPlaceholders("abc", null, "fesef"));
        assertThrows(IllegalArgumentException.class, () -> extractPlaceholders("abc", "   ", "fesef"));

        assertThrows(IllegalArgumentException.class, () -> extractPlaceholders("abc", "qwd", null));
        assertThrows(IllegalArgumentException.class, () -> extractPlaceholders("abc", "   ", " \t"));

        assertTrue(extractPlaceholders("some.string", "${","}").isEmpty());

        checkPrefixSuffix("${", "}");

        checkPrefixSuffix("{{", "}}");

        checkPrefixSuffix("<<", ">>");

        checkPrefixSuffix("<", ">");

        checkPrefixSuffix("XX", "XX");
    }

    private void checkPrefixSuffix(String prefix, String suffix) {
        assertEquals(
                MiscUtils.setOf("db.host", "db.port", "db.name"),
                extractPlaceholders("jdbc://" + prefix + "db.host" + suffix + ":" + prefix + "db.port" + suffix + "/" + prefix + "db.name" + suffix + "/schema", prefix, suffix)
        );

        assertEquals(
                MiscUtils.setOf("db.host", "db.port", "db.name"),
                extractPlaceholders("jdbc://" + prefix + "db.host" + suffix + ":" + prefix + "db.port" + suffix + "/" + prefix + "db.name" + suffix + "/schema" + prefix + "db.host" + suffix + ":" + prefix + "db.port" + suffix + "/" + prefix + "db.name" + suffix + "",
                        prefix, suffix)
        );

        assertEquals(
                MiscUtils.setOf("db", "db.host", "db.port", "db.name"),
                extractPlaceholders("jdbc://" + prefix + "db" + suffix + "" + prefix + "db" + suffix + "" + prefix + "db" + suffix + ":" + prefix + "db.port" + suffix + "/" + prefix + "db.name" + suffix + "/schema" + prefix + "db.host" + suffix + ":" + prefix + "db.port" + suffix + "/" + prefix + "db.name" + suffix + "",
                        prefix, suffix)
        );
    }
}
