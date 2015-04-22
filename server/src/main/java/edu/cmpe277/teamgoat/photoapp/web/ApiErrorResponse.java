package edu.cmpe277.teamgoat.photoapp.web;

/**
 * A class meant to be serialized to json, to inform the client of details about an error when making an api call.
 */
public class ApiErrorResponse {
    String message;
    String errorCode;
    boolean isError = true;

    public ApiErrorResponse(String message, String errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public boolean isError() {
        return isError;
    }

    public void setIsError(boolean isError) {
        this.isError = isError;
    }
}
