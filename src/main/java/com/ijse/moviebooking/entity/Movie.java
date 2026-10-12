package com.ijse.moviebooking.entity;

import com.ijse.moviebooking.enums.*;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Entity @Table(name = "movies") @Getter @Setter @NoArgsConstructor
public class Movie {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String title;
    @Column(length = 2000) private String description;
    @Column(nullable = false) private Integer durationMinutes;
    private String language;
    @Column(nullable = false) private String genre;
    private LocalDate releaseDate;
    private String posterUrl;
    private String rating;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private MovieStatus status = MovieStatus.NOW_SHOWING;
}
