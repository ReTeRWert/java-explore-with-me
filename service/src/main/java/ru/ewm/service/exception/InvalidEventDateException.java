package ru.ewm.service.exception;

public class InvalidEventDateException extends RuntimeException {
    public InvalidEventDateException(String message) {
        super(message);
    }
}
