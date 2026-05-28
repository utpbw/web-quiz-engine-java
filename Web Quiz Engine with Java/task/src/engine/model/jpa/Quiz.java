package engine.model.jpa;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity representing a persisted quiz.
 *
 * <p>Options are stored as a child {@link Option} collection ordered by
 * insertion index so GET responses always return options in the original order.
 * {@code createdBy} holds the email of the registered user who created the quiz,
 * used to enforce ownership on DELETE.</p>
 */
@Entity
@Table(name = "quiz")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String text;

    /**
     * Eagerly loaded to avoid LazyInitializationException outside a transaction.
     * Ordered by insertion index so API responses always return options in the original order.
     */
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true,
               fetch = FetchType.EAGER)
    @JoinColumn(name = "quiz_id", nullable = false)
    @OrderColumn(name = "option_index")
    private List<Option> options = new ArrayList<>();

    /** Email of the user who created this quiz; used for ownership checks on DELETE. */
    @Column(nullable = false)
    private String createdBy;

    public Quiz() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public List<Option> getOptions() { return options; }
    public void setOptions(List<Option> options) { this.options = options; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
