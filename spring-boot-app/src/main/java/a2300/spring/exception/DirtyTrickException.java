package a2300.spring.exception;

public class DirtyTrickException extends RuntimeException {
    public DirtyTrickException(String message) {
        super("Dirty Trick: " + message);
    }
}
