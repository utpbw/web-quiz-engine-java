package engine.model;

public class Result {
    private boolean success;
    private String feedback;

    Result(boolean success, String feedback) {
        this.success = success;
        this.feedback = feedback;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public static Result success() {
        return new Result(true, "Congratulations, you're right!");
    }

    public static Result wrong() {
        return new Result(false, "Wrong answer! Please, try again.");
    }

}
