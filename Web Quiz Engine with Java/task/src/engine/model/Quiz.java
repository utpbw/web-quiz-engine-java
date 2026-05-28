package engine.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a quiz question stored in the engine.
 *
 * <p>Bean Validation annotations enforce the creation contract:
 * title and text must be non-blank, options must have at least 2 entries.
 * The {@code answer} set is accepted on input but never returned in responses.</p>
 */
public class Quiz {

    /** Auto-assigned unique identifier; not accepted from client input. */
    private int id;

    /** Required; must not be blank. */
    @NotEmpty(message = "Quiz title must not be empty")
    @NotNull(message = "Quiz title must not be null")
    private String title;

    /** Required; must not be blank. */
    @NotEmpty(message = "Quiz text must not be empty")
    @NotNull(message = "Quiz text must not be null")
    private String text;

    /** Required; must contain at least 2 options. */
    @Size(min = 2, message = "Quiz must have at least 2 options")
    private String[] options;

    /**
     * Zero-based indices of all correct options.
     * {@code WRITE_ONLY} keeps this out of all JSON responses.
     * Defaults to an empty set when absent from the request body.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<Integer> answer = new HashSet<>();

    /** Required by Jackson for deserialization. */
    public Quiz() {
    }

    public Quiz(int id, String title, String text, String[] options, Set<Integer> answer) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.options = options;
        this.answer = answer;
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

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public Set<Integer> getAnswer() {
        return answer;
    }

    public void setAnswer(Set<Integer> answer) {
        this.answer = answer;
    }
}
