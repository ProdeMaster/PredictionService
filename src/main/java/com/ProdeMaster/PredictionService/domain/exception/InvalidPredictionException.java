package com.ProdeMaster.PredictionService.domain.exception;

public class InvalidPredictionException extends RuntimeException {
    public InvalidPredictionException(String message) {
        super(message);
    }

    public InvalidPredictionException(String message, Throwable cause) {
        super(message, cause);
    }
}