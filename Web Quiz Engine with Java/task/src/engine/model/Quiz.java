package engine.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a quiz question stored in the engine.
 *
 * <p>The {@code answer} field is accepted on input (POST body) but never
 * returned in responses, preventing clients from reading the correct answer.</p>
 */
public class Quiz {

    /** Auto-assigned unique identifier; not accepted from client input. */
    private int id;
    private String title;
    private String text;
    private String[] options;

    /**
     * Zero-based index of the correct option.
     * {@code WRITE_ONLY} keeps it out of all JSON responses.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int answer;

    /** Required by Jackson for deserialization. */
    public Quiz() {}

    /**
     * Constructs a quiz with all fields (used internally).
     *
     * @param id      unique identifier
     * @param title   short display name
     * @param text    question body
     * @param options answer choices
     * @param answer  zero-based index of the correct option
     */
    public Quiz(int id, String title, String text, String[] options, int answer) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.options = options;
        this.answer = answer;
    }

    /** @return unique quiz identifier */
    public int getId() { return id; }

    /** @param id assigned by {@link engine.service.QuizService} on creation */
    public void setId(int id) { this.id = id; }

    /** @return quiz title; may be {@code null} if not provided by client */
    public String getTitle() { return title; }

    /** @param title short display name */
    public void setTitle(String title) { this.title = title; }

    /** @return question text; may be {@code null} if not provided by client */
    public String getText() { return text; }

    /** @param text question body */
    public void setText(String text) { this.text = text; }

    /** @return answer option array; may be {@code null} if not provided by client */
    public String[] getOptions() { return options; }

    /** @param options array of answer choices */
    public void setOptions(String[] options) { this.options = options; }

    /**
     * Returns the zero-based index of the correct answer.
     * Not serialized to JSON ({@code WRITE_ONLY}).
     *
     * @return correct option index
     */
    public int getAnswer() { return answer; }

    /** @param answer zero-based index of the correct option */
    public void setAnswer(int answer) { this.answer = answer; }
}
