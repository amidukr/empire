package org.qik.empire.utils;

/**
 * Created by qik on 25.10.2014.
 */
public class VerificationException extends RuntimeException {

    public VerificationException(String message, Object ... arguments) {
        super(String.format(message, arguments));
    }
}
