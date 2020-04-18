# light-config
Lightweight config without additional java dependencies.

I've looked through several config libraries (cfg4j, typesafe, etc.) and unfortunately all of them don't satisfy me - 
there are a lot of additional dependencies, they work with YAML (God bless positioning...), they have unpredictable behavior.
Spring boot's alternative is a good one but it is included to spring boot...  
As a good mama's Java developer I had to write my own config!

## Usage 
Think about property source:
```java
        Map<String, String> propertiesMap = new HashMap<>();

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

```

Define some fancy interface
```java
public interface GoodAppConfig {

        String dbUrl();

        @LightConfigProperty("db.port")
        Integer getDbPort();

        @LightConfigPropertyDeserialize(ColorDeserializer.class)
        Color getDesktopColor();

        @LightConfigPropertyFormat(format = PATTERN)
        Date applicationUpdateDate();
    }
```  

Add some magic to your fancy pants:
```java
        GoodAppConfig appConfig = LightConfigManager.INSTANCE.load(new RecursiveSupplier(new MapSupplier(propertiesMap)), GoodAppConfig.class);

```

` RecursiveSupplier` is used here if you love recursive references in your properties.

Call methods and use it everywhere:
```java
        Assertions.assertEquals(5432, appConfig.getDbPort());
        Assertions.assertEquals("jdbc://127.0.0.1:5432/db", appConfig.dbUrl());
        Assertions.assertEquals(Color.GREEN, appConfig.getDesktopColor());

```
