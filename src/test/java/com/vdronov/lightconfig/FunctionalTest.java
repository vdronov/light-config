package com.vdronov.lightconfig;

import com.vdronov.lightconfig.annotations.LightConfigProperty;
import com.vdronov.lightconfig.annotations.LightConfigPropertyDeserialize;
import com.vdronov.lightconfig.annotations.LightConfigPropertyFormat;
import com.vdronov.lightconfig.conversion.ColorDeserializer;
import com.vdronov.lightconfig.suppliers.MapSupplier;
import com.vdronov.lightconfig.suppliers.RecursiveSupplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Vasiliy Dronov
 */
public class FunctionalTest {

    public static final String PATTERN = "dd/MM/yyyy";

    public interface GoodAppConfig {

        String dbUrl();

        @LightConfigProperty("db.port")
        Integer getDbPort();

        @LightConfigPropertyDeserialize(ColorDeserializer.class)
        Color getDesktopColor();

        @LightConfigPropertyFormat(format = PATTERN)
        Date applicationUpdateDate();
    }

    @Test
    void testHappyPath() throws ParseException {
        HashMap<String, String> propertiesMap = new HashMap<>();

        propertiesMap.put("db.url", "jdbc://${db.hostport}/${db.name}");
        propertiesMap.put("db.name", "db");
        propertiesMap.put("db.hostport", "${db.host}:${db.port}");
        propertiesMap.put("db.host", "${machine.ip}");
        propertiesMap.put("db.port", "5432");
        propertiesMap.put("db", "abrakadabra");
        propertiesMap.put("machine.ip", "127.0.0.1");
        propertiesMap.put("desktop.color", "GREEN");
        String updateDateAsStr = "10/12/3020";
        propertiesMap.put("application.update.date", updateDateAsStr);

        GoodAppConfig appConfig = LightConfigManager.INSTANCE.load(new RecursiveSupplier(new MapSupplier(propertiesMap)), GoodAppConfig.class);

        Assertions.assertEquals(5432, appConfig.getDbPort());
        Assertions.assertEquals("jdbc://127.0.0.1:5432/db", appConfig.dbUrl());
        Assertions.assertEquals(Color.GREEN, appConfig.getDesktopColor());

        SimpleDateFormat format = new SimpleDateFormat(PATTERN);
        Date updatingDate = format.parse(updateDateAsStr);
        Assertions.assertEquals(updatingDate, appConfig.applicationUpdateDate());



    }
}
