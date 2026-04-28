package uk.ac.westminster.smartcampus.api.exception;

public class RoomConflictException extends RuntimeException {
    public RoomConflictException(String message) {
        super(message);
    }
}
