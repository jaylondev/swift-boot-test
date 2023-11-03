package org.github.jaylondev.swift.boot.test.exception;

/**
 * @author jaylon 2023/8/7 22:58
 */
public class SwiftBootTestException extends RuntimeException {

    public SwiftBootTestException(String message) {
        super(message);
    }

    public SwiftBootTestException(String message, Throwable cause) {
        super(message, cause);
    }

    public SwiftBootTestException(Throwable cause) {
        super(cause);
    }

}
