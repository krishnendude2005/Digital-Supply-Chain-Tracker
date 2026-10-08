package com.SupplyChain.DigitalSupplyChainTracker.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex) {

        log.error("Resource not found: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                "No resource found",
                HttpStatus.NOT_FOUND.value()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

    }

    // Handle user not found
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                "User not found",
                HttpStatus.NOT_FOUND.value()
        );

        log.error("User not found: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UserNotMatch.class)
    public ResponseEntity<?> handleUserNotMath(UserNotMatch ex) {
        log.error("User not match: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                "User not match",
                HttpStatus.NOT_FOUND.value()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(CheckpointLogNotFound.class)
    public ResponseEntity<?> handleCheckpointLogNotFoundException(Exception ex) {
        log.error("Checkpoint log not found: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                "Checkpoint log not found",
                HttpStatus.NO_CONTENT.value()
        );

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(error);
    }

    @ExceptionHandler(ShipmentAlreadyExistsException.class)
    public ResponseEntity<?> handleShipmentAlreadyExistsException(Exception ex) {
        log.error("Shipment already exists: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                "Shipment already exists",
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
