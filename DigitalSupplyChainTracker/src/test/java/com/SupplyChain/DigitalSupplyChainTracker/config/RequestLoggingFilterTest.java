package com.SupplyChain.DigitalSupplyChainTracker.config;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestLoggingFilterTest {

    private final RequestLoggingFilter filter = new RequestLoggingFilter();

    @Test
    void doFilter_LogsMethodUriStatusAndDuration() throws Exception {
        Logger logger = (Logger) LoggerFactory.getLogger(RequestLoggingFilter.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/items");
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setStatus(200);

        try {
            filter.doFilter(request, response, new MockFilterChain());
        } finally {
            logger.detachAppender(appender);
        }

        assertEquals(1, appender.list.size());
        String message = appender.list.get(0).getFormattedMessage();
        assertTrue(message.contains("GET"), "expected method in log but was: " + message);
        assertTrue(message.contains("/items"), "expected URI in log but was: " + message);
        assertTrue(message.contains("200"), "expected status in log but was: " + message);
        assertTrue(message.endsWith("ms"), "expected duration suffix in log but was: " + message);
    }

    @Test
    void doFilter_InvokesRestOfFilterChain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/shipments");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean chainInvoked = new AtomicBoolean(false);

        FilterChain chain = (servletRequest, servletResponse) -> chainInvoked.set(true);

        filter.doFilter(request, response, chain);

        assertTrue(chainInvoked.get(), "filter chain should continue processing the request");
    }
}
