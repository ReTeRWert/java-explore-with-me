package ru.ewm.stats.server.exception;

import lombok.Data;

@Data
public class ErrorResponse {
    private final String message;
}
