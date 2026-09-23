package com.edunest.backend.modules.coupon.entity;

import java.time.LocalDateTime;
import com.edunest.backend.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "coupon_usages",
        uniqueConstraints = @UniqueConstraint(name = "uk_coupon_usage_user_coupon", columnNames = {"coupon_id", "user_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CouponUsage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false) private Integer usageCount;
    @Column(nullable = false) private LocalDateTime lastUsedAt;
}