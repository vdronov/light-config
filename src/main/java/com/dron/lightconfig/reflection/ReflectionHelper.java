package com.dron.lightconfig.reflection;

import com.dron.lightconfig.annotations.LightConfigProperty;
import com.dron.lightconfig.annotations.LightConfigPropertyDeserialize;
import com.dron.lightconfig.annotations.LightConfigPropertyFormat;
import com.dron.lightconfig.utils.MiscUtils;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Helper class to work with reflection
 *
 * @author Vasiliy Dronov
 */
public class ReflectionHelper {

    //singleton
    public static ReflectionHelper INSTANCE = new ReflectionHelper();

    /**
     * Set of possible return types - temporal limitation
     */
    private static final Set<Class<?>> POSSIBLE_RETURN_TYPES = MiscUtils.setOf(
            Date.class, Number.class, String.class, Enum.class
    );

    private ReflectionHelper() {
    }

    /**
     * Fetches metadata of declared config methods
     *
     * @param declaredMethods declared methods
     * @return map: method name to method metadata
     */
    public Map<String, MethodInfo> getDeclaredMethodsMetadata(Method[] declaredMethods) {
        if (MiscUtils.isEmpty(declaredMethods)) {
            throw new IllegalArgumentException("config class must have methods - otherwise it is useless");
        }

        Map<String, MethodInfo> resultMap = new HashMap<>(declaredMethods.length, 1F);
        for (Method declaredMethod : declaredMethods) {
            assertParametersAreEmpty(declaredMethod);

            validateReturnType(declaredMethod);

            MethodInfo methodInfo = extractMetadata(declaredMethod);

            resultMap.put(methodInfo.getName(), methodInfo);
        }
        return resultMap;
    }

    /**
     * Fetches all required metadata from method
     */
    private MethodInfo extractMetadata(Method declaredMethod) {
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setName(declaredMethod.getName());

        LightConfigProperty lightConfigProperty = declaredMethod.getAnnotation(LightConfigProperty.class);
        if (lightConfigProperty != null && MiscUtils.isNotBlank(lightConfigProperty.value())) {
            methodInfo.setPropertyName(lightConfigProperty.value());
        } else {
            methodInfo.setPropertyName(extractPropertyName(methodInfo.getName()));
        }

        LightConfigPropertyFormat configPropertyFormat = declaredMethod.getAnnotation(LightConfigPropertyFormat.class);
        if (configPropertyFormat != null && MiscUtils.isNotBlank(configPropertyFormat.format())) {
            methodInfo.setFormat(configPropertyFormat.format());
        }

        LightConfigPropertyDeserialize configPropertyDeserialize = declaredMethod.getAnnotation(LightConfigPropertyDeserialize.class);
        if (configPropertyDeserialize != null) {
            methodInfo.setCustomDeserializer(configPropertyDeserialize.value());
        }

        methodInfo.setReturnType(declaredMethod.getReturnType());
        return methodInfo;
    }


    /**
     * Makes property name from method name
     *
     * @param methodName name of the method
     * @return name of the property
     */
    private String extractPropertyName(String methodName) {

        String propertyName = MiscUtils.replaceCamelCaseWithDots(methodName);

        if (propertyName.startsWith("get.")) {
            propertyName = propertyName.substring(4); //yes yes hardcode right here
        }

        return propertyName;
    }

    /**
     * Validates the return type of the method
     */
    private void validateReturnType(Method method) {
        Class<?> returnType = method.getReturnType();
        for (Class<?> possibleReturnType : POSSIBLE_RETURN_TYPES) {
            if (possibleReturnType.isAssignableFrom(returnType)) {
                return;
            }
        }
        if (method.getAnnotation(LightConfigPropertyDeserialize.class) != null) {
            return;
        }

        String methodName = method.getName();
        throw new IllegalArgumentException(
                "Return type of " + methodName + " must be one of the following:" + POSSIBLE_RETURN_TYPES
                        + ". Or custom deserializer must be present, see " + LightConfigPropertyDeserialize.class.getName()
        );
    }

    /**
     * Checks that method's parameters are empty
     *
     * @param declaredMethod some method
     */
    private void assertParametersAreEmpty(Method declaredMethod) {
        if (MiscUtils.isNotEmpty(declaredMethod.getParameters())) {
            throw new IllegalArgumentException("Methods of config can't have parameters - check method " + declaredMethod.getName());
        }
    }

}
