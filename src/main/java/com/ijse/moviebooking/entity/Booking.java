package com.ijse.moviebooking.entity;

import com.ijse.moviebooking.enums.*;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Entity @Table(name = "bookings") @Getter @Setter @NoArgsConstructor
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    /** Booking M:1 User */
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id") private User user;
    /** Booking M:1 Show */
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "show_id") private Show show;
    /** Selected seat numbers, e.g. A1, A2 (stored in table booking_seats) */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "booking_seats", joinColumns = @JoinColumn(name = "booking_id"))
    @Column(name = "seat_number") private List<String> seatNumbers = new ArrayList<>();
    @Column(nullable = false) private Integer numberOfTickets;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal totalAmount;
    @Column(nullable = false) private LocalDateTime bookingDate;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private BookingStatus status = BookingStatus.PENDING;
}
