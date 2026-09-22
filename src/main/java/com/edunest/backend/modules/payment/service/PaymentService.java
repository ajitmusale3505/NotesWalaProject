package com.edunest.backend.modules.payment.service;

import com.edunest.backend.modules.payment.dto.request.VerifyPaymentRequest;

public interface PaymentService {

    String verifyPayment(VerifyPaymentRequest request);
}