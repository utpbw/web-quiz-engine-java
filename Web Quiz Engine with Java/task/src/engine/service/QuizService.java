package engine.service;

import engine.exception.QuizNotFoundException;
import engine.model.dto.QuizDto;
import engine.model.dto.ResultDto;
import engine.model.jpa.Quiz;
import engine.repository.QuizRepository;
import engine.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Service layer bridging the REST API and the JPA repository.
 *
 * <p>Converts between {@link QuizDto} (API contract) and {@link Quiz} (JPA entity),
 * delegating persistence to {@link QuizRepository}.</p>
 */
@Service
public class QuizService {

    private final QuizRepository quizRepository;

    /** @param quizRepository H2-backed repository, injected by Spring */
    @Autowired
    public QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    /**
     * Validates answer indices, converts to entity, persists, and returns the DTO with its new ID.
     *
     * @param quizDto validated quiz from the request body
     * @return the same DTO with its server-assigned {@code id} populated
     */
    public QuizDto addQuizToStorage(QuizDto quizDto) {
        Utils.checkAnswerOptions(quizDto);
        Quiz quizEntity = Utils.convertQuizDtoToEntity(quizDto);
        int id = quizRepository.save(quizEntity).getId();
        quizDto.setId(id);
        return quizDto;
    }

    /**
     * Returns all stored quizzes as DTOs (answer field excluded from JSON).
     *
     * @return list of all quizzes; empty when none have been created
     */
    public List<QuizDto> getAllQuizzesFromStorage() {
        List<QuizDto> quizzesDto = new ArrayList<>();
        for (Quiz each : quizRepository.findAll()) {
            quizzesDto.add(Utils.convertEntityToQuizDto(each));
        }
        return quizzesDto;
    }

    /**
     * Returns a single quiz DTO by ID.
     *
     * @param id quiz identifier
     * @return the matching quiz DTO
     * @throws QuizNotFoundException if no quiz with the given ID exists
     */
    public QuizDto getQuizById(int id) {
        return Utils.convertEntityToQuizDto(findById(id));
    }

    /**
     * Loads the JPA entity by ID, throwing 404 if absent.
     *
     * @param id quiz identifier
     * @return the JPA entity
     * @throws QuizNotFoundException if not found
     */
    public Quiz findById(int id) {
        return quizRepository.findById(id)
            .orElseThrow(QuizNotFoundException::new);
    }

    /**
     * Evaluates the submitted answer set against the stored correct answers.
     *
     * <p>Comparison is set-based: order does not matter.</p>
     *
     * @param id     quiz identifier
     * @param answer set of submitted option indices
     * @return {@link ResultDto#success()} if the sets match, {@link ResultDto#wrong()} otherwise
     * @throws QuizNotFoundException if no quiz with the given ID exists
     */
    public ResultDto solveQuizById(int id, Set<Integer> answer) {
        Quiz quizEntity = findById(id);
        Set<Integer> correctAnswer = Utils.getIndexOfAnswer(quizEntity.getOptions());
        return correctAnswer.equals(answer) ? ResultDto.success() : ResultDto.wrong();
    }
}
