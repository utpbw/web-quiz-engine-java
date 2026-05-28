package engine.model.jpa;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity representing a persisted quiz.
 *
 * <p>Options are stored as a child {@link Option} collection ordered by
 * insertion index so GET responses always return options in the original order.</p>
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

    /** Preserves insertion order so the API always returns options in order. */
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @JoinColumn(name = "quiz_id", nullable = false)
    @OrderColumn(name = "option_index")
    private List<Option> options = new ArrayList<>();

    public Quiz() {
    }

    public Quiz(int id, String title, String text, List<Option> options) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.options = options;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }
}