package com.fiap.auditservice;

import com.fiap.auditservice.test.config.TestKafkaConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import com.fiap.auditservice.test.config.TestSecurityConfig;

@ActiveProfiles("test")
@SpringBootTest
@Import({TestKafkaConfig.class, TestSecurityConfig.class})
class AuditServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}