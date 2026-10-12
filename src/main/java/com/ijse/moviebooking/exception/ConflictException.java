package com.ijse.moviebooking.exception;

/** Request conflicts with existing data, e.g. seat already booked (HTTP 409). */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) { super(message); }
}
