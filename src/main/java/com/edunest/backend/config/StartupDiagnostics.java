package com.edunest.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupDiagnostics {

    private static final Logger log = LoggerFactory.getLogger(StartupDiagnostics.class);

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${cloudflare.r2.endpoint}")
    private String r2Endpoint;

    @Value("${cloudflare.r2.bucket}")
    private String r2Bucket;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${app.cors.allowed-origins}")
    private String corsOrigins;

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        log.info("============================================================");
        log.info("EduNest backend is READY");
        log.info("checkpoint.database={}", sanitizeDatabaseUrl(datasourceUrl));
        log.info("checkpoint.r2.endpointConfigured={}", r2Endpoint != null && !r2Endpoint.isBlank());
        log.info("checkpoint.r2.bucketConfigured={}", r2Bucket != null && !r2Bucket.isBlank());
        log.info("checkpoint.jwt.secretLength={}", jwtSecret == null ? 0 : jwtSecret.length());
        log.info("checkpoint.cors.configured={}", corsOrigins != null && !corsOrigins.isBlank());
        log.info("checkpoint.swagger=/swagger-ui.html");
        log.info("============================================================");
    }

    private String sanitizeDatabaseUrl(String url) {
        if (url == null) return "not-configured";
        return url.replaceAll("(?i)(password=)[^&;]*", "$1***");
    }
}
