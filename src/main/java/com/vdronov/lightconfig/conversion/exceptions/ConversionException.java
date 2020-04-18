package com.vdronov.lightconfig.conversion.exceptions;

/**
 * @author Vasiliy Dronov
 */
public class ConversionException extends IllegalStateException {

    public ConversionException() {
    }

    public ConversionException(String s) {
        super(s);
    }

    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConversionException(Throwable cause) {
        super(cause);
    }
}
