package com.ijse.moviebooking.entity;

import com.ijse.moviebooking.enums.*;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Entity @Table(name = "theatres") @Getter @Setter @NoArgsConstructor
public class Theatre {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String name;
    @Column(nullable = false) private String location;
    @Column(nullable = false) private Integer capacity;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private TheatreStatus status = TheatreStatus.ACTIVE;
}
