package com.cookbook.exception;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class CookBookExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        ExceptionBody exceptionBody = new ExceptionBody("Input validation failed", ex.getMessage());
        return new ResponseEntity<>(exceptionBody, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ExceptionBody exceptionBody = new ExceptionBody("Input validation failed", ex.getMessage());
        return new ResponseEntity<>(exceptionBody, HttpStatus.BAD_REQUEST);
    }



    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Object> handlePersistFailureException(ServiceException persistFailureException) {
        ExceptionBody exceptionBody = new ExceptionBody("Unable to persist the Data", persistFailureException.getMessage());
        return new ResponseEntity<>(exceptionBody, HttpStatus.INTERNAL_SERVER_ERROR);

    }


    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleNoSuchElementException(NoSuchElementException noSuchElementException) {
        ExceptionBody exceptionBody = new ExceptionBody("No Records found for the input criteria", noSuchElementException.getMessage());
        return new ResponseEntity<>(exceptionBody, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Object> handleExpiredJwtException(ExpiredJwtException expiredJwtException) {
        ExceptionBody exceptionBody = new ExceptionBody("JWT Token Expired", expiredJwtException.getMessage());
        return new ResponseEntity<>(exceptionBody, HttpStatus.BAD_REQUEST);

    }
}
