package engine.utils;

import engine.exception.InvalidAnswerException;
import engine.model.dto.QuizDto;
import engine.model.jpa.Option;
import engine.model.jpa.Quiz;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Stateless utility helpers for quiz validation and DTO/entity conversion.
 */
public class Utils {

    private Utils() {}

    /**
     * Validates that every answer index in the DTO is within the bounds of its options list.
     *
     * <p>Called after Bean Validation passes, so {@code options} is non-null with
     * at least 2 elements. Any index outside {@code [0, options.size())} triggers 400
     * via {@link InvalidAnswerException}.</p>
     *
     * @param quizDto the quiz DTO whose answer indices to validate
     * @throws InvalidAnswerException if any index is out of range
     */
    public static void checkAnswerOptions(QuizDto quizDto) {
        if (quizDto.getOptions() == null) {
            throw new InvalidAnswerException();
        }
        int size = quizDto.getOptions().size();
        for (Integer index : quizDto.getAnswer()) {
            if (index < 0 || index >= size) {
                throw new InvalidAnswerException();
            }
        }
    }

    /**
     * Converts a {@link QuizDto} (request body) to a {@link Quiz} JPA entity.
     *
     * <p>Each option string becomes an {@link Option} entity; the {@code answer}
     * flag is set on options whose index appears in the DTO's answer set.</p>
     *
     * @param quizDto source DTO
     * @return a new, unpersisted {@link Quiz} entity ready for {@code repository.save()}
     */
    public static Quiz convertQuizDtoToEntity(QuizDto quizDto) {
        Quiz quiz = new Quiz();
        quiz.setTitle(quizDto.getTitle());
        quiz.setText(quizDto.getText());
        List<Option> options = new ArrayList<>();
        for (int i = 0; i < quizDto.getOptions().size(); i++) {
            Option option = new Option();
            option.setText(quizDto.getOptions().get(i));
            option.setAnswer(quizDto.getAnswer().contains(i));
            options.add(option);
        }
        quiz.setOptions(options);
        return quiz;
    }

    /**
     * Converts a persisted {@link Quiz} entity to a {@link QuizDto} response object.
     *
     * <p>The {@code answer} field is intentionally not set here; {@code QuizDto.answer}
     * is {@code WRITE_ONLY} so it will not appear in JSON responses regardless.</p>
     *
     * @param quizEntity source JPA entity
     * @return a {@link QuizDto} with id, title, text, and option strings
     */
    public static QuizDto convertEntityToQuizDto(Quiz quizEntity) {
        QuizDto quizDto = new QuizDto();
        quizDto.setId(quizEntity.getId());
        quizDto.setTitle(quizEntity.getTitle());
        quizDto.setText(quizEntity.getText());
        quizDto.setOptions(quizEntity.getOptions().stream()
            .map(Option::getText)
            .collect(Collectors.toList()));
        return quizDto;
    }

    /**
     * Derives the set of correct answer indices from an ordered list of {@link Option} entities.
     *
     * @param options the option list in insertion order
     * @return set of zero-based indices where {@code option.getAnswer() == true}
     */
    public static Set<Integer> getIndexOfAnswer(List<Option> options) {
        Set<Integer> answer = new HashSet<>();
        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).getAnswer()) {
                answer.add(i);
            }
        }
        return answer;
    }
}
