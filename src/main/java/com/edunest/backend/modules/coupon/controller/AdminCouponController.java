package com.edunest.backend.modules.coupon.controller;
import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.coupon.dto.request.AdminCreateCouponRequest;
import com.edunest.backend.modules.coupon.dto.response.CouponResponse;
import com.edunest.backend.modules.coupon.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/admin/coupons") @RequiredArgsConstructor @PreAuthorize("hasRole('ADMIN')")
public class AdminCouponController {
 private final CouponService couponService;
 @PostMapping public ResponseEntity<ApiResponse<CouponResponse>> create(@Valid @RequestBody AdminCreateCouponRequest r){return ok("Coupon created successfully",couponService.create(r));}
 @GetMapping public ResponseEntity<ApiResponse<Page<CouponResponse>>> all(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){int s=Math.min(Math.max(size,1),50);return ResponseEntity.ok(ApiResponse.<Page<CouponResponse>>builder().success(true).message("Coupons fetched successfully").data(couponService.getAll(PageRequest.of(Math.max(page,0),s,Sort.by(Sort.Order.desc("id"))))).build());}
 @PutMapping("/{id}") public ResponseEntity<ApiResponse<CouponResponse>> update(@PathVariable Long id,@Valid @RequestBody AdminCreateCouponRequest r){return ok("Coupon updated successfully",couponService.update(id,r));}
 @DeleteMapping("/{id}") public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id){couponService.deactivate(id);return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Coupon deactivated successfully").build());}
 private <T> ResponseEntity<ApiResponse<T>> ok(String m,T d){return ResponseEntity.ok(ApiResponse.<T>builder().success(true).message(m).data(d).build());}
}