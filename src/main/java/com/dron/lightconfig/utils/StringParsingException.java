package com.dron.lightconfig.utils;

/**
 * @author Vasiliy Dronov
 */
public class StringParsingException extends RuntimeException {

    private final String originalString;

    private final int position;

    public StringParsingException(String originalString, int position, String errorMessage) {
        super(errorMessage);
        this.originalString = originalString;
        this.position = position;
    }

}
