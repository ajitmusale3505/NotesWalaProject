package com.edunest.backend.modules.payment.controller;

import org.springframework.web.bind.annotation.*;

import com.edunest.backend.modules.payment.dto.request.VerifyPaymentRequest;
import com.edunest.backend.modules.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/verify")
    public String verifyPayment(
            @Valid @RequestBody VerifyPaymentRequest request) {

        return paymentService.verifyPayment(request);
    }
}