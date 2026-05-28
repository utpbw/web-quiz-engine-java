package engine.model.jpa;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA entity recording one successful quiz completion by a user.
 *
 * <p>A user may complete the same quiz multiple times; each attempt gets its
 * own row. Completions are sorted by {@code completedAt} descending when
 * returned to clients.</p>
 */
@Entity
@Table(name = "quiz_completion")
public class QuizCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    /** Email of the user who solved the quiz. */
    @Column(nullable = false)
    private String userEmail;

    /** ID of the quiz that was correctly answered. */
    @Column(nullable = false)
    private int quizId;

    /** Timestamp of the correct answer; populated by the service layer. */
    @Column(nullable = false)
    private LocalDateTime completedAt;

    public QuizCompletion() {}

    public long getId() { return id; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public int getQuizId() { return quizId; }
    public void setQuizId(int quizId) { this.quizId = quizId; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
