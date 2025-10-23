package com.application.brokagefirm.infrastructure.persistence.entity;

import com.application.brokagefirm.domain.enums.OrderSide;
import com.application.brokagefirm.domain.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    private String assetName;

    @Enumerated(EnumType.STRING)
    private OrderSide orderSide;

    private BigDecimal size;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime createDate;
}
