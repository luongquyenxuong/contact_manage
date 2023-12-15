package com.example.manage_contacts.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@RestControllerAdvice
@Slf4j
public class HandlerException extends ResponseEntityExceptionHandler {

    @ExceptionHandler({BaseException.class})
    public ResponseEntity<ErrorResponse> handleBaseEx(BaseException e) {
        log.error("Handling Exception: {}", e.getMessage());

        HttpStatus httpStatus = HttpStatus.valueOf(e.getStatus());

        return ResponseEntity.status(httpStatus)
                .body(ErrorResponse.create(e, HttpStatus.valueOf(e.getStatus()), e.getMessage()));

    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorResponse> handleEx(BaseException e) {
        log.error("Handling Exception: {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.create(e,HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));


    }

}
