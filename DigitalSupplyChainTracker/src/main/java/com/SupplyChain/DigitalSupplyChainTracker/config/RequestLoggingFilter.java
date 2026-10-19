package com.SupplyChain.DigitalSupplyChainTracker.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.nanoTime();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = (System.nanoTime() - startTime) / 1_000_000;
            int status = response.getStatus();
            String method = request.getMethod();
            String uri = request.getRequestURI();

            String message = String.format("%s %s - status=%d - duration=%d ms", method, uri, status, durationMs);

            if (status >= 500) {
                logger.error(message);
            } else if (status == 401 || status == 403) {
                logger.warn(message);
            } else if (status >= 400) {
                logger.warn(message);
            } else {
                logger.info(message);
            }
        }
    }
}
