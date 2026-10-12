package com.ijse.moviebooking.entity;

import com.ijse.moviebooking.enums.*;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Entity @Table(name = "users") @Getter @Setter @NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String name;
    @Column(nullable = false, unique = true) private String email;
    @Column(nullable = false) private String password;
    private String phone;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private Role role;

    /** User 1:M Booking */
    @OneToMany(mappedBy = "user") private List<Booking> bookings = new ArrayList<>();
}
