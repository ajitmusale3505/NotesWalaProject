package com.edunest.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EdunestApplicationTests {

    @Test
    void applicationContextLoads() {
        // Startup itself is a smoke test for dependency wiring, JPA mappings,
        // security configuration and controller registration.
    }
}
