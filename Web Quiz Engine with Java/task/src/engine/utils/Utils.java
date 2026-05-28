package engine.utils;

import engine.exception.InvalidAnswerException;
import engine.model.Quiz;

/**
 * Stateless utility helpers for quiz validation.
 */
public class Utils {

    private Utils() {}

    /**
     * Validates that every answer index in the quiz is within the bounds of its options array.
     *
     * <p>Called after Bean Validation passes, so {@code options} is guaranteed non-null
     * with at least 2 elements. Any answer index outside {@code [0, options.length)}
     * triggers a 400 response via {@link InvalidAnswerException}.</p>
     *
     * @param quiz the quiz whose answer indices to validate
     * @throws InvalidAnswerException if any answer index is out of range
     */
    public static void checkAnswerOptions(Quiz quiz) {
        if (quiz.getOptions() == null) {
            throw new InvalidAnswerException();
        }
        int optionCount = quiz.getOptions().length;
        for (Integer index : quiz.getAnswer()) {
            if (index < 0 || index >= optionCount) {
                throw new InvalidAnswerException();
            }
        }
    }
}
