package org.example.exception;

public class ExecutionException extends RuntimeException {

    public ExecutionException(Throwable cause) {
        super(cause);
    }

    public ExecutionException(String message) {
        super(message);
    }

    public ExecutionException() {
        super();
    }
}
