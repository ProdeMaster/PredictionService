package com.ProdeMaster.PredictionService.domain.exception;

public class PredictionNotFoundException extends RuntimeException {
    public PredictionNotFoundException(String message) {
        super(message);
    }

    public PredictionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}