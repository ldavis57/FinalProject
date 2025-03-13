package dar.member.controller.error;

import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

/**
 * Global error handler for REST controllers.
 * Captures and handles specific exceptions to provide meaningful HTTP responses.
 */
@RestControllerAdvice // Marks this class as a global exception handler for REST controllers
@Slf4j // Enables logging using Lombok
public class GlobalErrorHandler {
    
    /**
     * Handles NoSuchElementException globally.
     * Logs the exception and returns an HTTP 404 status with an error message.
     * 
     * @param ex The NoSuchElementException that was thrown.
     * @return A map containing the error message.
     */
    @ExceptionHandler(NoSuchElementException.class) // Catches NoSuchElementException
    @ResponseStatus(code = HttpStatus.NOT_FOUND) // Returns HTTP 404 Not Found status
    public Map<String, String> handleNoSuchElementException(NoSuchElementException ex) {
        log.error("Exception: {}", ex.toString()); // Logs the exception details
        return Map.of("message", ex.toString()); // Returns a simple JSON response with the error message
    }
}
