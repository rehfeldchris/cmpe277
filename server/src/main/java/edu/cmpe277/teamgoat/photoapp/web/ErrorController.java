package edu.cmpe277.teamgoat.photoapp.web;

import edu.cmpe277.teamgoat.photoapp.errors.BadApiRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@RestController
public class ErrorController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = BadApiRequestException.class)
    public Map<String, String> handleRestApiException(HttpServletRequest request, HttpServletResponse response, BadApiRequestException exception) {
        Map<String, String> data = new HashMap<>();

        return data;
    }
}
