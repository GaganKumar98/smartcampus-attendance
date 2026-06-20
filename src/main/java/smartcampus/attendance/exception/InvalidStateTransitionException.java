package smartcampus.attendance.exception;

public class InvalidStateTransitionException extends RuntimeException {

    public InvalidStateTransitionException(String message) {
        super(message);
    }

    public InvalidStateTransitionException(String entityName, Object fromState, Object toState) {
        super("Invalid state transition for " + entityName + ": " + fromState + " -> " + toState);
    }
}
