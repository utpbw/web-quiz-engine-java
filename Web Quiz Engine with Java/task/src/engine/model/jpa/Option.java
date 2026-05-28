package engine.model.jpa;

import jakarta.persistence.*;

/**
 * JPA entity representing one answer option within a {@link Quiz}.
 *
 * <p>The {@code answer} flag marks whether this option is a correct answer,
 * eliminating the need to store answer indices separately.</p>
 */
@Entity
@Table(name = "option")
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "option_id")
    private int id;

    @Column(name = "option_text")
    private String text;

    @Column(name = "isAnswer")
    private boolean answer;

    public Option() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean getAnswer() {
        return answer;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }
}
