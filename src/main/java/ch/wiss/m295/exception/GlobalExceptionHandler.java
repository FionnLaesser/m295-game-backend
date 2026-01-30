package ch.wiss.m295.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/**
 * Zentrale Fehlerbehandlung für fachliche Fehler (404 / 409).
 * Security-Fehler (401 / 403) werden bewusst NICHT hier behandelt,
 * sondern von Spring Security übernommen.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Behandelt fachliche Fehler wie NOT_FOUND (404) und CONFLICT (409).
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {

        // Nur 404 und 409 behandeln – alles andere Spring überlassen
        int status = ex.getStatusCode().value();
        if (status != 404 && status != 409) {
            throw ex;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status);
        body.put("error", ex.getReason());

        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }
}
