package com.ProdeMaster.PredictionService.domain.exception;

public class PredictionAlreadyExistsException extends RuntimeException {
    public PredictionAlreadyExistsException(String message) {
        super(message);
    }

    public PredictionAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}