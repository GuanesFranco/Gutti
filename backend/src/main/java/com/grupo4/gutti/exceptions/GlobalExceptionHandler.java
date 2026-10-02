package com.grupo4.gutti.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) {
        // Criterio de aceptación: Mensaje genérico de error
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas. Verifica tu email y contraseña.");
    }

    /** Errores de validación de los DTOs (@NotNull, @Positive, etc.): se devuelven todos los mensajes juntos. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        String mensajes = ex.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .distinct()
                .collect(Collectors.joining(". "));
        return ResponseEntity.badRequest().body(mensajes);
    }

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<String> handleStockInsuficienteException(StockInsuficienteException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    // Errores con código propio (404 no existe, 409 conflicto): devuelve ese código y el mensaje.
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
    }

    // JSON mal escrito o con un valor que no existe (por ejemplo tipoDeEntrega "AVION").
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleJsonInvalido(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body("El JSON enviado no es válido o tiene un valor incorrecto");
    }

    // Parámetro de la URL que falta o con un valor incorrecto (por ejemplo una fecha mal escrita).
    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<String> handleParametroInvalido(Exception ex) {
        return ResponseEntity.badRequest().body("Falta un parámetro o tiene un valor incorrecto");
    }
}
