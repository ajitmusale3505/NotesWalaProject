package com.edunest.backend.modules.payment.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.enums.OrderStatus;
import com.edunest.backend.common.enums.PaymentStatus;
import com.edunest.backend.modules.order.entity.Order;
import com.edunest.backend.modules.order.repository.OrderRepository;
import com.edunest.backend.modules.payment.dto.request.VerifyPaymentRequest;
import com.edunest.backend.modules.payment.entity.Payment;
import com.edunest.backend.modules.payment.repository.PaymentRepository;
import com.edunest.backend.modules.payment.service.PaymentService;
import com.edunest.backend.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Value("${payment.razorpay.webhook-secret:}")
    private String razorpaySecret;

    @Override
    @Transactional
    public String verifyPayment(VerifyPaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new BadRequestException("Order not found"));

        if (!SecurityUtils.isAdmin() &&
                !SecurityUtils.getCurrentUserId().equals(order.getUser().getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Order does not belong to the authenticated user");
        }

        Payment payment = paymentRepository.findByOrder(order)
                .orElseThrow(() -> new BadRequestException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return "Payment already verified";
        }

        if (razorpaySecret == null || razorpaySecret.isBlank()) {
            throw new IllegalStateException("Payment verification secret is not configured");
        }

        if (request.getProviderOrderId() == null || request.getProviderPaymentId() == null
                || request.getProviderSignature() == null) {
            throw new BadRequestException("Incomplete payment verification data");
        }

        // Razorpay-style verification: HMAC_SHA256(providerOrderId + "|" + providerPaymentId, secret)
        String payload = request.getProviderOrderId() + "|" + request.getProviderPaymentId();
        if (!constantTimeEquals(request.getProviderSignature(), hmacSha256(payload, razorpaySecret))) {
            throw new BadRequestException("Invalid payment signature");
        }

        // Prevent a payment for another gateway order being attached to this order.
        if (payment.getProviderOrderId() != null
                && !payment.getProviderOrderId().equals(request.getProviderOrderId())) {
            throw new BadRequestException("Provider order mismatch");
        }

        payment.setProviderPaymentId(request.getProviderPaymentId());
        payment.setProviderOrderId(request.getProviderOrderId());
        payment.setProviderSignature(request.getProviderSignature());
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());

        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());
        order.setPaymentReference(request.getProviderPaymentId());

        paymentRepository.save(payment);
        orderRepository.save(order);

        return "Payment verified successfully";
    }

    private static String hmacSha256(String payload, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Unable to verify payment signature", e);
        }
    }

    private static boolean constantTimeEquals(String a, String b) {
        return java.security.MessageDigest.isEqual(
                a.getBytes(StandardCharsets.UTF_8),
                b.getBytes(StandardCharsets.UTF_8));
    }
}
