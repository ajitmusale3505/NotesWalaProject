package com.edunest.backend.modules.bootstrap.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.bootstrap.dto.BootstrapResponse;
import com.edunest.backend.modules.bootstrap.service.BootstrapService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/bootstrap")
@RequiredArgsConstructor
public class BootstrapController {

    private final BootstrapService bootstrapService;

    @GetMapping
    public ResponseEntity<ApiResponse<BootstrapResponse>> getBootstrap() {

        BootstrapResponse data =
                bootstrapService.getBootstrapData();

        ApiResponse<BootstrapResponse> response =
                ApiResponse.<BootstrapResponse>builder()
                        .success(true)
                        .message("Bootstrap data fetched successfully")
                        .data(data)
                        .build();

        return ResponseEntity.ok(response);
    }
}