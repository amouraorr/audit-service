package com.fiap.auditservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

class AuditServiceApplicationTests {

    @Test
    @DisplayName("Deve chamar SpringApplication.run ao executar o main")
    void main_shouldCallSpringApplicationRun() {
        // Arrange
        String[] args = new String[0];

        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(AuditServiceApplication.class, args)).thenReturn(null);

            // Act
            AuditServiceApplication.main(args);

            // Assert
            mocked.verify(() -> SpringApplication.run(AuditServiceApplication.class, args), times(1));
        }
    }

    @Test
    @DisplayName("Deve conter a anotação SpringBootApplication com scanBasePackages configurado")
    void class_shouldBeAnnotatedWithSpringBootApplicationAndScanBasePackages() {
        // Arrange
        SpringBootApplication annotation = AuditServiceApplication.class.getAnnotation(SpringBootApplication.class);

        // Act
        String[] scanBasePackages = annotation != null ? annotation.scanBasePackages() : new String[0];

        // Assert
        assertNotNull(annotation, "A anotação SpringBootApplication deve estar presente na classe");
        boolean containsPackage = false;
        for (String pkg : scanBasePackages) {
            if ("com.fiap.auditservice".equals(pkg)) {
                containsPackage = true;
                break;
            }
        }
        assertTrue(containsPackage, "scanBasePackages deve conter 'com.fiap.auditservice'");
    }
}