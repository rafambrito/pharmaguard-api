package com.pharmaguard.api.shared.infrastructure.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class CorrelationIdLoggingFilterTest {

    private static final Pattern UUID_PATTERN = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$"
    );

    @Test
    void shouldReuseIncomingCorrelationId() throws ServletException, IOException {
        CorrelationIdLoggingFilter filter = new CorrelationIdLoggingFilter();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test/logging/ping");
        MockHttpServletResponse response = new MockHttpServletResponse();

        String incomingCorrelationId = "client-correlation-id-123";
        request.addHeader(CorrelationIdLoggingFilter.CORRELATION_ID_HEADER, incomingCorrelationId);

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getHeader(CorrelationIdLoggingFilter.CORRELATION_ID_HEADER))
                .isEqualTo(incomingCorrelationId);
    }

    @Test
    void shouldGenerateCorrelationIdWhenMissing() throws ServletException, IOException {
        CorrelationIdLoggingFilter filter = new CorrelationIdLoggingFilter();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test/logging/ping");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        String generatedCorrelationId = response.getHeader(CorrelationIdLoggingFilter.CORRELATION_ID_HEADER);
        assertThat(generatedCorrelationId).isNotBlank();
        assertThat(UUID_PATTERN.matcher(generatedCorrelationId).matches()).isTrue();
    }

    @Test
    void shouldLogRequestAndResponseWithCorrelationId() throws ServletException, IOException {
        CorrelationIdLoggingFilter filter = new CorrelationIdLoggingFilter();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test/logging/ping");
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader(CorrelationIdLoggingFilter.CORRELATION_ID_HEADER, "logging-correlation-id-456");

        Logger logger = (Logger) LoggerFactory.getLogger(CorrelationIdLoggingFilter.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        try {
            filter.doFilter(request, response, new MockFilterChain());
        } finally {
            logger.detachAppender(listAppender);
        }

        List<ILoggingEvent> events = listAppender.list;

        assertThat(events)
                .anyMatch(event -> event.getFormattedMessage().contains("event=request_received"));
        assertThat(events)
                .anyMatch(event -> event.getFormattedMessage().contains("event=response_sent"));
        assertThat(events)
                .anyMatch(event -> "logging-correlation-id-456".equals(event.getMDCPropertyMap().get("correlationId")));
    }
}
