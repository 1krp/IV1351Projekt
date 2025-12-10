package se.kth.iv1351.bankjdbc.model;

/**
 * Thrown when a teacher exceeds the maximum number of allowed courses
 * for a given period.
 */
public class AllocationLimitExceededException extends RuntimeException {

    public AllocationLimitExceededException(String message) {
        super(message);
    }

    public AllocationLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}