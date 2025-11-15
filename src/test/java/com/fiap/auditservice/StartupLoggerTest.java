package com.fiap.auditservice;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StartupLoggerTest {

    private static class TestAppender extends AppenderBase<ILoggingEvent> {
        private final List<ILoggingEvent> events = new ArrayList<>();

        @Override
        protected void append(ILoggingEvent eventObject) {
            events.add(eventObject);
        }

        List<ILoggingEvent> getEvents() {
            return events;
        }
    }

    @Test
    @DisplayName("Deve logar 'default' quando não existem perfis ativos")
    void onReady_noActiveProfiles_logsDefault() {
        // Arrange
        Environment env = Mockito.mock(Environment.class);
        Mockito.when(env.getActiveProfiles()).thenReturn(new String[0]);
        StartupLogger startupLogger = new StartupLogger(env);

        Logger lbLogger = (Logger) LoggerFactory.getLogger(StartupLogger.class);
        TestAppender appender = new TestAppender();
        appender.setContext(lbLogger.getLoggerContext());
        appender.start();
        lbLogger.addAppender(appender);

        try {
            // Act
            startupLogger.onReady();

            // Assert
            List<ILoggingEvent> events = appender.getEvents();
            assertFalse(events.isEmpty(), "Deve haver pelo menos um evento de log");
            boolean containsDefault = events.stream()
                    .anyMatch(e -> e.getFormattedMessage().contains("default"));
            assertTrue(containsDefault, "Mensagem de log deve conter 'default' quando não há perfis ativos");
        } finally {
            // Verify
            lbLogger.detachAppender(appender);
            appender.stop();
        }
    }

    @Test
    @DisplayName("Deve logar 'ok' quando existem perfis ativos")
    void onReady_withActiveProfiles_logsOk() {
        // Arrange
        Environment env = Mockito.mock(Environment.class);
        Mockito.when(env.getActiveProfiles()).thenReturn(new String[]{"test", "integration"});
        StartupLogger startupLogger = new StartupLogger(env);

        Logger lbLogger = (Logger) LoggerFactory.getLogger(StartupLogger.class);
        TestAppender appender = new TestAppender();
        appender.setContext(lbLogger.getLoggerContext());
        appender.start();
        lbLogger.addAppender(appender);

        try {
            // Act
            startupLogger.onReady();

            // Assert
            List<ILoggingEvent> events = appender.getEvents();
            assertFalse(events.isEmpty(), "Deve haver pelo menos um evento de log");
            boolean containsOk = events.stream()
                    .anyMatch(e -> e.getFormattedMessage().contains("ok"));
            assertTrue(containsOk, "Mensagem de log deve conter 'ok' quando há perfis ativos");
        } finally {
            // Verify
            lbLogger.detachAppender(appender);
            appender.stop();
        }
    }
}