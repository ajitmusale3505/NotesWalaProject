package com.edunest.backend.modules.test.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test/secure")
    public String secureEndpoint(Authentication authentication) {
        return "JWT authentication successful : " + authentication.getName();
    }
}