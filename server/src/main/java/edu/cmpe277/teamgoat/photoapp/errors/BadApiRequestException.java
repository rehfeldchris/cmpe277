package edu.cmpe277.teamgoat.photoapp.errors;


public class BadApiRequestException extends Exception {
    public BadApiRequestException() {
        super();
    }

    public BadApiRequestException(String message) {
        super(message);
    }

    public BadApiRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadApiRequestException(Throwable cause) {
        super(cause);
    }

    protected BadApiRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
