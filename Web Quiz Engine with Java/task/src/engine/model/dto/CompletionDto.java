package engine.model.dto;

import java.time.LocalDateTime;

/**
 * Data-transfer object for a single quiz completion, returned by
 * {@code GET /api/quizzes/completed}.
 *
 * <p>The {@code id} is the quiz ID (not the completion record ID).
 * {@code completedAt} is serialized as an ISO datetime string by Jackson.</p>
 */
public class CompletionDto {

    /** ID of the quiz that was successfully answered. */
    private int id;

    /** Timestamp when the correct answer was submitted. */
    private LocalDateTime completedAt;

    public CompletionDto() {}

    public CompletionDto(int id, LocalDateTime completedAt) {
        this.id = id;
        this.completedAt = completedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
