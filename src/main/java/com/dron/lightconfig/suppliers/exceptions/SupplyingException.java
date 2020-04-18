package com.dron.lightconfig.suppliers.exceptions;

/**
 * Exception for
 *
 * @author Vasiliy Dronov
 */
public class SupplyingException extends IllegalStateException {

    public SupplyingException() {
    }

    public SupplyingException(String s) {
        super(s);
    }

    public SupplyingException(String message, Throwable cause) {
        super(message, cause);
    }

    public SupplyingException(Throwable cause) {
        super(cause);
    }
}
