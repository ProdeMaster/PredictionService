package com.ProdeMaster.PredictionService.api.exception;

/**
 * Thrown when the authenticated userId from the {@code X-User-Id} header does
 * not match the userId present in the request body or path variable.
 *
 * <p>Indicates an attempt to act on behalf of another user. Mapped to HTTP 403 Forbidden.
 */
public class ForbiddenAccessException extends RuntimeException {

    public ForbiddenAccessException(String message) {
        super(message);
    }
}
