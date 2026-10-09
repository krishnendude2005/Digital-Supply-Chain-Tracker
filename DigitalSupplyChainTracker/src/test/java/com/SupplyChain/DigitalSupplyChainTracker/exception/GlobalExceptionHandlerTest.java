package com.SupplyChain.DigitalSupplyChainTracker.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ThrowingController())
                .setControllerAdvice(handler)
                .build();
    }

    // ----------------- direct handler unit tests -----------------

    @Test
    void handleResourceNotFoundException_Returns404WithGenericMessage() {
        ResponseEntity<?> response = handler.handleResourceNotFoundException(
                new ResourceNotFoundException("Item with id X not found"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse body = assertInstanceOf(ErrorResponse.class, response.getBody());
        assertEquals("No resource found", body.getMessage());
        assertEquals(404, body.getStatus());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleUserNotFoundException_Returns404WithMessage() {
        ResponseEntity<?> response = handler.handleUserNotFoundException(
                new UserNotFoundException("john@example.com"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse body = assertInstanceOf(ErrorResponse.class, response.getBody());
        assertEquals("User not found", body.getMessage());
        assertEquals(404, body.getStatus());
    }

    @Test
    void handleUserNotMatch_Returns404WithMessage() {
        ResponseEntity<?> response = handler.handleUserNotMath(
                new UserNotMatch("User does not own this resource"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse body = assertInstanceOf(ErrorResponse.class, response.getBody());
        assertEquals("User not match", body.getMessage());
        assertEquals(404, body.getStatus());
    }

    @Test
    void handleCheckpointLogNotFoundException_Returns204WithMessage() {
        ResponseEntity<?> response = handler.handleCheckpointLogNotFoundException(
                new CheckpointLogNotFound("Shipment not started yet"));

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        ErrorResponse body = assertInstanceOf(ErrorResponse.class, response.getBody());
        assertEquals("Checkpoint log not found", body.getMessage());
        assertEquals(204, body.getStatus());
    }

    @Test
    void handleShipmentAlreadyExistsException_Returns400WithMessage() {
        ResponseEntity<?> response = handler.handleShipmentAlreadyExistsException(
                new ShipmentAlreadyExistsException("Shipment already exists for item"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = assertInstanceOf(ErrorResponse.class, response.getBody());
        assertEquals("Shipment already exists", body.getMessage());
        assertEquals(400, body.getStatus());
    }

    // ------------- end-to-end mapping through MockMvc -------------

    @Test
    void resourceNotFound_IsMappedTo404JsonError() throws Exception {
        mockMvc.perform(get("/test/resource"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No resource found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void userNotFound_IsMappedTo404JsonError() throws Exception {
        mockMvc.perform(get("/test/user"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void userNotMatch_IsMappedTo404JsonError() throws Exception {
        mockMvc.perform(get("/test/user-match"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not match"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void checkpointLogNotFound_IsMappedTo204() throws Exception {
        mockMvc.perform(get("/test/checkpoint"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.message").value("Checkpoint log not found"))
                .andExpect(jsonPath("$.status").value(204));
    }

    @Test
    void shipmentAlreadyExists_IsMappedTo400JsonError() throws Exception {
        mockMvc.perform(get("/test/shipment"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Shipment already exists"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @RestController
    static class ThrowingController {

        @GetMapping("/test/resource")
        void resource() {
            throw new ResourceNotFoundException("Resource missing");
        }

        @GetMapping("/test/user")
        void user() {
            throw new UserNotFoundException("User missing");
        }

        @GetMapping("/test/user-match")
        void userMatch() {
            throw new UserNotMatch("User mismatch");
        }

        @GetMapping("/test/checkpoint")
        void checkpoint() {
            throw new CheckpointLogNotFound("Checkpoint missing");
        }

        @GetMapping("/test/shipment")
        void shipment() {
            throw new ShipmentAlreadyExistsException("Shipment exists");
        }
    }
}
