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
}
