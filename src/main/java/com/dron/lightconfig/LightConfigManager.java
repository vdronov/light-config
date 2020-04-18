package com.dron.lightconfig;

import com.dron.lightconfig.proxy.CachingProxyHandler;
import com.dron.lightconfig.reflection.MethodInfo;
import com.dron.lightconfig.reflection.ReflectionHelper;
import com.dron.lightconfig.suppliers.RawPropertiesSupplier;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author Vasiliy Dronov
 */
public class LightConfigManager {

    public static final LightConfigManager INSTANCE = new LightConfigManager();


    private LightConfigManager() {
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> T load(RawPropertiesSupplier rawPropertiesSupplier, Class<T> configType) {
        validate(rawPropertiesSupplier, configType);

        Method[] declaredMethods = configType.getDeclaredMethods();

        Map<String, MethodInfo> methodsMap = ReflectionHelper.INSTANCE.getDeclaredMethodsMetadata(declaredMethods);

        Object config = Proxy.newProxyInstance(configType.getClassLoader(),
                new Class[]{configType},
                new CachingProxyHandler(rawPropertiesSupplier, methodsMap)
        );

        return (T) config;
    }


    /**
     * Validates the input parameters
     */
    private <T> void validate(RawPropertiesSupplier rawPropertiesSupplier, Class<T> configType) {
        if (rawPropertiesSupplier == null) {
            throw new NullPointerException("properties provider must be passed");
        }
        if (configType == null) {
            throw new NullPointerException("config class must be passed, otherwise we don't know how build the config");
        }
        if (!configType.isInterface()) {
            throw new IllegalArgumentException("config class must be interface");
        }

    }

}
