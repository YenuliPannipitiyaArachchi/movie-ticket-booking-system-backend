package com.ijse.moviebooking.entity;

import com.ijse.moviebooking.enums.*;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Entity @Table(name = "payments") @Getter @Setter @NoArgsConstructor
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    /** Booking 1:1 Payment */
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "booking_id", unique = true) private Booking booking;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal amount;
    private LocalDateTime paymentDate;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private PaymentMethod method;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private PaymentStatus status = PaymentStatus.PENDING;
}
