package shelter.exceptions;

public class InvalidPetOperationException extends Exception {
   
    private final String errorCode;

    // ── Constructeurs ────────────────────────────────────────────────────────
    public InvalidPetOperationException(String message) {
        super(message);
        this.errorCode = "INVALID_OPERATION";
    }

    public InvalidPetOperationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public InvalidPetOperationException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }


    public InvalidPetOperationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "INVALID_OPERATION";
    }

    // ── Getter ───────────────────────────────────────────────────────────────

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "[" + errorCode + "] " + getMessage();
    }
}
