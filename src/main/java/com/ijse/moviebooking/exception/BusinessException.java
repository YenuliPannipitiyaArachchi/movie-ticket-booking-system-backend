package com.ijse.moviebooking.exception;

/** A business rule was broken (HTTP 400). */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) { super(message); }
}
