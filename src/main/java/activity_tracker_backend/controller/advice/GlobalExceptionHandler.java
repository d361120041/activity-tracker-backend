package activity_tracker_backend.controller.advice;

import activity_tracker_backend.controller.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException e) {
        Response response = new Response();
        response.setMessage(e.getMessage());
        log.error("MethodArgumentNotValidException: {}", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException e) {
        Response response = new Response();
        response.setMessage(e.getMessage());
        log.error("RuntimeException: {}", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }
}
