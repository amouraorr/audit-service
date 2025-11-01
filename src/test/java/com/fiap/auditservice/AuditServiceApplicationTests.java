package com.fiap.auditservice;

import com.fiap.auditservice.test.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestSecurityConfig.class)
class AuditServiceApplicationTests {

    @Test
    void contextLoads() {

    }
}