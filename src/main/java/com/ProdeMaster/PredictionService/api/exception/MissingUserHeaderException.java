package com.ProdeMaster.PredictionService.api.exception;

/**
 * Thrown when the {@code X-User-Id} header injected by the API Gateway is
 * absent or blank on an endpoint that requires user identity.
 *
 * <p>A missing header indicates the request bypassed the Gateway, which is
 * not a valid flow in production. Mapped to HTTP 400 Bad Request.
 */
public class MissingUserHeaderException extends RuntimeException {

    public MissingUserHeaderException(String message) {
        super(message);
    }
}
