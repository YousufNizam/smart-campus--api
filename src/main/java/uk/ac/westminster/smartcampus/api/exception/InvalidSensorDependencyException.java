package uk.ac.westminster.smartcampus.api.exception;

public class InvalidSensorDependencyException extends RuntimeException {
    public InvalidSensorDependencyException(String message) {
        super(message);
    }
}
