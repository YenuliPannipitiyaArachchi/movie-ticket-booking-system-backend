package com.ijse.moviebooking.dto;

import com.ijse.moviebooking.enums.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;

/** Request / response DTOs grouped in one file for readability. */
public final class Dtos {
    private Dtos() {}

    // ---- Auth ----
    public record SignUpRequest(@NotBlank String name, @NotBlank @Email String email,
                                @NotBlank @Size(min = 6, message = "must be at least 6 characters") String password,
                                @Pattern(regexp = "^$|^\\+?\\d{9,15}$", message = "must be 9-15 digits") String phone) {}
    public record SignInRequest(@NotBlank @Email String email, @NotBlank String password) {}
    public record AuthResponse(String token, String role, String name, String email) {}

    // ---- Users ----
    public record UserUpdateRequest(@NotBlank String name, String phone, Role role) {}
    public record UserResponse(Long id, String name, String email, String phone, Role role) {}

    // ---- Movies ----
    public record MovieRequest(@NotBlank String title, String description,
                               @NotNull @Positive Integer durationMinutes, String language,
                               @NotBlank String genre, LocalDate releaseDate, String posterUrl,
                               String rating, MovieStatus status) {}
    public record MovieResponse(Long id, String title, String description, Integer durationMinutes, String language,
                                String genre, LocalDate releaseDate, String posterUrl, String rating, MovieStatus status) {}

    // ---- Theatres ----
    public record TheatreRequest(@NotBlank String name, @NotBlank String location,
                                 @NotNull @Positive Integer capacity, TheatreStatus status) {}
    public record TheatreResponse(Long id, String name, String location, Integer capacity, TheatreStatus status) {}

    // ---- Shows ----
    public record ShowRequest(@NotNull Long movieId, @NotNull Long theatreId, @NotNull LocalDate showDate,
                              @NotNull LocalTime showTime, @NotNull @Positive BigDecimal ticketPrice, ShowStatus status) {}
    public record ShowResponse(Long id, Long movieId, String movieTitle, Long theatreId, String theatreName,
                               LocalDate showDate, LocalTime showTime, BigDecimal ticketPrice, ShowStatus status,
                               Integer totalSeats, List<String> bookedSeats) {}

    // ---- Bookings ----
    public record BookingRequest(@NotNull Long showId, @NotEmpty(message = "must contain at least one seat") List<@NotBlank String> seatNumbers,
                                 @NotNull @Positive Integer numberOfTickets) {}
    public record BookingStatusRequest(@NotNull BookingStatus status) {}
    public record BookingResponse(Long id, Long showId, String movieTitle, String theatreName, LocalDate showDate,
                                  LocalTime showTime, List<String> seatNumbers, Integer numberOfTickets,
                                  BigDecimal totalAmount, LocalDateTime bookingDate, BookingStatus status,
                                  String paymentStatus, String customerEmail) {}

    // ---- Payments ----
    public record PaymentRequest(@NotNull Long bookingId, @NotNull PaymentMethod method) {}
    public record PaymentResponse(Long id, Long bookingId, BigDecimal amount, PaymentMethod method,
                                  PaymentStatus status, LocalDateTime paidAt) {}
    public record PaymentStatusResponse(Long paymentId, PaymentStatus status) {}
}
