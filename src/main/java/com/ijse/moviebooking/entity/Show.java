package com.ijse.moviebooking.entity;

import com.ijse.moviebooking.enums.*;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Entity @Table(name = "shows") @Getter @Setter @NoArgsConstructor
public class Show {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    /** Show M:1 Movie */
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "movie_id") private Movie movie;
    /** Show M:1 Theatre */
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "theatre_id") private Theatre theatre;
    @Column(nullable = false) private LocalDate showDate;
    @Column(nullable = false) private LocalTime showTime;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal ticketPrice;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private ShowStatus status = ShowStatus.SCHEDULED;
}
