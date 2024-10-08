package com.mon_rec_sys.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.mon_rec_sys.payload.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> resourceNotFoundeExceptionHandler(ResourceNotFoundException exception){
        String message = exception.getMessage();
        ApiResponse response = new ApiResponse(message, false);
        return new ResponseEntity<ApiResponse>(response, HttpStatus.NOT_FOUND);
    }

}
