package com.ProdeMaster.PredictionService.api.exception;

/**
 * Thrown when the userId from the path variable is not a member of the requested group.
 *
 * <p>Mapped to HTTP 404 Not Found: the user does not exist within this group's context,
 * so surfacing a 403 would leak information about group membership.
 */
public class UserNotInGroupException extends RuntimeException {

    public UserNotInGroupException(String message) {
        super(message);
    }
}
