package edu.ccrm.exceptions;

public class MaxCreditLimitExceededException extends Exception {
    private final int currentCredits;
    private final int maxCredits;
    private final int attemptedCredits;

    public MaxCreditLimitExceededException(String message, int currentCredits, int maxCredits, int attemptedCredits) {
        super(message);
        this.currentCredits = currentCredits;
        this.maxCredits = maxCredits;
        this.attemptedCredits = attemptedCredits;
    }

    public int getCurrentCredits() { return currentCredits; }
    public int getMaxCredits() { return maxCredits; }
    public int getAttemptedCredits() { return attemptedCredits; }
}
