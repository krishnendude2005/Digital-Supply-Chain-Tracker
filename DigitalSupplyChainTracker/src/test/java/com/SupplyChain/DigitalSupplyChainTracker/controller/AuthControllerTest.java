package com.SupplyChain.DigitalSupplyChainTracker.controller;

import com.SupplyChain.DigitalSupplyChainTracker.dto.request.LoginRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.LoginResponse;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.Role;
import com.SupplyChain.DigitalSupplyChainTracker.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void login_shouldReturn200AndLoginResponse() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@gmail.com");
        request.setPassword("123");

        LoginResponse response = LoginResponse.builder()
                .token("jwt-token")
                .role(Role.ADMIN)
                .email("admin@gmail.com")
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.email").value("admin@gmail.com"));
    }
}

