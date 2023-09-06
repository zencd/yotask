package svc.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import svc.exception.IncorrectRequestException;
import svc.exception.NotFoundException;

@ControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> error404(NotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IncorrectRequestException.class)
    public ResponseEntity<String> error400(IncorrectRequestException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
