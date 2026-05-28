package engine.model;

/**
 * Represents a single quiz question with multiple-choice options.
 *
 * <p>Instances are serialized to JSON by Jackson when returned from
 * REST endpoints, so every field needs a public getter.</p>
 */
public class Quiz {
    private String title;
    private String text;
    private String[] options;

    /**
     * Constructs a new quiz question.
     *
     * @param title   short display name for the quiz
     * @param text    the question body shown to the user
     * @param options array of answer choices (must contain at least one entry)
     */
    public Quiz(String title, String text, String[] options) {
        this.title = title;
        this.text = text;
        this.options = options;
    }

    /**
     * Returns the quiz title.
     *
     * @return non-null title string
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the question text.
     *
     * @return non-null question body
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the answer options array.
     *
     * @return array of option strings; never {@code null}
     */
    public String[] getOptions() {
        return options;
    }

    /**
     * Sets the quiz title.
     *
     * @param title new title; must not be blank
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the question text.
     *
     * @param text new question body; must not be blank
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Replaces the answer options.
     *
     * @param options new array of option strings
     */
    public void setOptions(String[] options) {
        this.options = options;
    }
}
