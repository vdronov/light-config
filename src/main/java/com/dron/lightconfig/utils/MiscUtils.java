package com.dron.lightconfig.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Vasiliy Dronov
 */
public class MiscUtils {

    /**
     * Checks that collection is empty
     *
     * @param collection some collection
     * @param <T>        type of elements
     * @return true if collection is empty, false otherwise
     */
    @SuppressWarnings("unused")
    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Checks that array is empty
     *
     * @param array some array
     * @param <T>   type of elements
     * @return true if array is empty, false otherwise
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Checks that array is not empty
     *
     * @param array some array
     * @param <T>   type of elements
     * @return true if array is not empty, false otherwise
     */
    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }

    /**
     * Checks that string is empty.
     * Alternative of Apache string utils but we should avoid additional dependencies
     *
     * @return true if string is empty, false otherwise
     */
    public static boolean isBlank(String someString) {
        return someString == null || someString.trim().isEmpty();
    }

    /**
     * Checks that string is not empty.
     *
     * @return true if string is not empty, false otherwise
     */
    public static boolean isNotBlank(String someString) {
        return !isBlank(someString);
    }

    /**
     * Creates set of passed values
     *
     * @param values some values
     * @param <T>    type of supposed values
     * @return set of passed values
     */
    @SafeVarargs
    public static <T> Set<T> setOf(T... values) {
        if (isEmpty(values)) {
            return Collections.emptySet();
        }

        Set<T> resultSet = new HashSet<>(values.length, 1F);
        Collections.addAll(resultSet, values);
        return resultSet;
    }

    /**
     * Replaces camel case combinations in input string with dots.
     * Example: SomeString -> some.string
     *
     * @param s some string
     * @return string where all camel case combinations replaced with dots
     * @throws StringParsingException when it's hard to understand what word should be added with dots e.g: getABD
     */
    public static String replaceCamelCaseWithDots(String s) {
        if (isBlank(s) || s.length() == 1) {
            return s;
        }
        StringBuilder builder = new StringBuilder(s.length() + 5);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isLowerCase(c)) {
                builder.append(c);
                continue;
            }
            if (i != 0) {
                builder.append('.');
            }
            if (i < s.length() - 1) {
                if (Character.isUpperCase(s.charAt(i + 1))) {
                    throw new StringParsingException(s, i, "Two letters in upper case in a row");
                }
            }
            builder.append(Character.toLowerCase(c));
        }
        return builder.toString();
    }

    /**
     * Extracts keys from placeholders
     *
     * @param s                 some string with set of placeholders (may be empty)
     * @param placeholderPrefix string indicating start of placeholder
     * @param placeholderSuffix string indicating end of placeholder
     * @return set of keys specified in placeholders
     */
    public static Set<String> extractPlaceholders(String s, String placeholderPrefix, String placeholderSuffix) {
        if (isBlank(s)) {
            return Collections.emptySet();
        }
        if (isBlank(placeholderPrefix)) {
            throw new IllegalArgumentException("placeholder prefix should be specified");
        }

        if (isBlank(placeholderSuffix)) {
            throw new IllegalArgumentException("placeholder suffix should be specified");
        }

        Set<String> result = new HashSet<>();
        for (int end = 0, begin = 0; begin != -1 && end != -1; ) {
            begin = s.indexOf(placeholderPrefix, begin);
            if (begin != -1) {
                begin += placeholderPrefix.length();
                end = s.indexOf(placeholderSuffix, begin);
                if (end != -1) {
                    result.add(s.substring(begin, end));
                    begin = end + placeholderSuffix.length();
                }
            }
        }
        return result;
    }


}
