package com.vdronov.lightconfig.suppliers;

import com.vdronov.lightconfig.suppliers.exceptions.CircularDependencyAdditionException;
import com.vdronov.lightconfig.suppliers.exceptions.SupplyingException;
import com.vdronov.lightconfig.utils.MiscUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Optional;

/**
 * @author Vasiliy Dronov
 */
public class RecursiveSupplierTest {

    @Test
    void testCircularDependency() {
        HashMap<String, String> map = new HashMap<>();

        map.put("db.url", "jdbc://${db.hostport}/${db.name}_${db.port}");
        map.put("db.name", "db");
        map.put("db.hostport", "${db.host}:${db.port}");
        map.put("db.host", "localhost${db.url}");
        map.put("db.port", "5432");
        map.put("db", "abrakadabra");

        RecursiveSupplier uut = new RecursiveSupplier(new MapSupplier(map));

        Assertions.assertEquals("${", uut.getPrefix());
        Assertions.assertEquals("}", uut.getSuffix());

        try {
            uut.supply("db.url");
        } catch (CircularDependencyAdditionException e) {
            Assertions.assertEquals("db.host", e.getParentKey());
            Assertions.assertEquals("db.url", e.getChildKey());
            Assertions.assertEquals(new TreePath(MiscUtils.setOf("db.url", "db.hostport")), e.getTreePath());

        }

        Assertions.assertThrows(CircularDependencyAdditionException.class, () -> uut.supply("db.url"));
    }

    @Test
    void testCircularDependency2() {
        HashMap<String, String> map = new HashMap<>();

        map.put("db.url", "jdbc://${db.hostport}/${db.name}");
        map.put("db.name", "${db.host}_${machine.ip}");
        map.put("db.hostport", "${db.host}:${db.port}");
        map.put("db.host", "${machine.ip}${db.port}");
        map.put("db.port", "5432");
        map.put("db", "abrakadabra");
        map.put("machine.ip", "${db.name}");

        RecursiveSupplier uut = new RecursiveSupplier(new MapSupplier(map));

        try {
            uut.supply("db.url");
        } catch (CircularDependencyAdditionException e) {
            Assertions.assertEquals("machine.ip", e.getParentKey());
            Assertions.assertEquals("db.name", e.getChildKey());
            Assertions.assertEquals(new TreePath(MiscUtils.setOf("db.url", "db.name", "db.host")), e.getTreePath());
        }

    }

    @Test
    void testUnresolvable() {
        HashMap<String, String> map = new HashMap<>();

        map.put("db.url", "jdbc://${db.hostport}/${db.name}_${db.port}");
        map.put("db.name", "db");
        map.put("db.hostport", "${db.host}:${db.port}");
        map.put("db.host", "localhost");

        map.put("db", "abrakadabra");

        RecursiveSupplier uut = new RecursiveSupplier(new MapSupplier(map));

        Assertions.assertEquals("${", uut.getPrefix());
        Assertions.assertEquals("}", uut.getSuffix());

        Assertions.assertThrows(SupplyingException.class, () -> uut.supply("db.url"));
    }

    @Test
    void testHappyPath() {
        HashMap<String, String> map = new HashMap<>();

        map.put("db.url", "jdbc://${db.hostport}/${db.name}_${db.port}__jdbc://${db.hostport}/${db.name}_${db.port}");
        map.put("db.name", "db");
        map.put("db.hostport", "${db.host}:${db.port}");
        map.put("db.host", "localhost");
        map.put("db.port", "5432");
        map.put("db", "abrakadabra");

        RecursiveSupplier uut = new RecursiveSupplier(new MapSupplier(map));

        Optional<String> supply = uut.supply("db.url");

        Assertions.assertTrue(supply.isPresent());

        Assertions.assertEquals("jdbc://localhost:5432/db_5432__jdbc://localhost:5432/db_5432", supply.get());
    }

    @Test
    void testHappyPath2() {
        HashMap<String, String> map = new HashMap<>();

        map.put("db.url", "jdbc://${db.hostport}/${db.name}");
        map.put("db.name", "${db.host}_${machine.ip}");
        map.put("db.hostport", "${db.host}:${db.port}");
        map.put("db.host", "${machine.ip}${db.port}");
        map.put("db.port", "5432");
        map.put("db", "abrakadabra");
        map.put("machine.ip", "127.0.0.1");

        RecursiveSupplier uut = new RecursiveSupplier(new MapSupplier(map));

        Optional<String> supply = uut.supply("db.url");

        Assertions.assertTrue(supply.isPresent());

        Assertions.assertEquals("jdbc://127.0.0.15432:5432/127.0.0.15432_127.0.0.1", supply.get());
    }

    @Test
    void testBoundary() {
        HashMap<String, String> map = new HashMap<>();

        RecursiveSupplier uut = new RecursiveSupplier(new MapSupplier(map));
        Optional<String> supply = uut.supply("db.url");
        Assertions.assertFalse(supply.isPresent());

        map.put("db.url", "jdbc://localhost:5432/db");
        Assertions.assertEquals("jdbc://localhost:5432/db", uut.supply("db.url").orElse(null));

        Assertions.assertThrows(NullPointerException.class, () -> new RecursiveSupplier(null));
    }


}
