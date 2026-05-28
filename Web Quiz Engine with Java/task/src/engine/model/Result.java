package engine.model;

/**
 * Represents the outcome of a quiz answer submission.
 *
 * <p>Use the static factory methods {@link #success()} and {@link #wrong()}
 * rather than constructing instances directly.</p>
 */
public class Result {
    private boolean success;
    private String feedback;

    /**
     * Package-private constructor; use {@link #success()} or {@link #wrong()}.
     *
     * @param success  {@code true} if the submitted answer was correct
     * @param feedback human-readable message to display to the user
     */
    Result(boolean success, String feedback) {
        this.success = success;
        this.feedback = feedback;
    }

    /**
     * Returns whether the submitted answer was correct.
     *
     * @return {@code true} for a correct answer
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Returns the feedback message for the user.
     *
     * @return non-blank feedback string
     */
    public String getFeedback() {
        return feedback;
    }

    /**
     * Sets the success flag.
     *
     * @param success {@code true} if the answer is correct
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Sets the feedback message.
     *
     * @param feedback non-blank message string
     */
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    /**
     * Creates a {@code Result} indicating a correct answer.
     *
     * @return result with {@code success=true} and a congratulatory message
     */
    public static Result success() {
        return new Result(true, "Congratulations, you're right!");
    }

    /**
     * Creates a {@code Result} indicating a wrong answer.
     *
     * @return result with {@code success=false} and a retry prompt
     */
    public static Result wrong() {
        return new Result(false, "Wrong answer! Please, try again.");
    }

}
