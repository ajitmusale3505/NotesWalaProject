package com.edunest.backend.modules.coupon.controller;
import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.coupon.dto.request.CouponValidationRequest;
import com.edunest.backend.modules.coupon.dto.response.CouponValidationResponse;
import com.edunest.backend.modules.coupon.service.CouponService;
import com.edunest.backend.security.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/coupons") @RequiredArgsConstructor
public class CouponController {
 private final CouponService couponService;
 @PostMapping("/validate")
 public ResponseEntity<ApiResponse<CouponValidationResponse>> validate(@Valid @RequestBody CouponValidationRequest request){
  return ResponseEntity.ok(ApiResponse.<CouponValidationResponse>builder().success(true).message("Coupon validated successfully").data(couponService.validate(SecurityUtils.getCurrentUserId(),request)).build());
 }
}