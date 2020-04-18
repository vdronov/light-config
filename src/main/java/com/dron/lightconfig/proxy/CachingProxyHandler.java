package com.dron.lightconfig.proxy;

import com.dron.lightconfig.reflection.MethodInfo;
import com.dron.lightconfig.conversion.SimpleTypeConversionHelper;
import com.dron.lightconfig.suppliers.RawPropertiesSupplier;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Caching invocation handler to not calculate and convert types twice.
 * Can be invalidated thread safely using {@link #invalidate()} method
 *
 * @author Vasiliy Dronov
 */
public class CachingProxyHandler implements InvocationHandler {

    private ConcurrentHashMap<String, Object> typedCache;

    private Map<String, MethodInfo> methodInfoMap;

    private RawPropertiesSupplier rawPropertiesSupplier;

    public CachingProxyHandler(RawPropertiesSupplier rawPropertiesSupplier, Map<String, MethodInfo> methodInfoMap) {
        typedCache = new ConcurrentHashMap<>(50);
        this.methodInfoMap = methodInfoMap;
        this.rawPropertiesSupplier = rawPropertiesSupplier;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        String methodName = method.getName();
        MethodInfo methodInfo = methodInfoMap.get(methodName);
        String propertyName = methodInfo.getPropertyName();
        return typedCache.computeIfAbsent(propertyName,
                s -> rawPropertiesSupplier.supply(s).map(
                        value -> SimpleTypeConversionHelper.INSTANCE.parseString(value, methodInfo)
                ).orElse(null)
        );
    }

    /**
     * Invalidates the cache
     */
    @SuppressWarnings("unused")
    public void invalidate() {
        typedCache.clear();
    }


}
