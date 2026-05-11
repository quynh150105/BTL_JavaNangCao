package org.acme.Exception;

public class MovieException extends RuntimeException {
    private final int status;

    public MovieException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int status() {
        return status;
    }
}
