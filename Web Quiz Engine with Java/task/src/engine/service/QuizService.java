package engine.service;

import engine.exception.QuizNotFoundException;
import engine.model.dto.QuizDto;
import engine.model.dto.ResultDto;
import engine.model.jpa.Quiz;
import engine.repository.QuizRepository;
import engine.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Service layer bridging the REST API and the JPA repository.
 *
 * <p>Converts between {@link QuizDto} (API contract) and {@link Quiz} (JPA entity),
 * delegating persistence to {@link QuizRepository}. Quiz creation records the creator's
 * email for ownership enforcement on deletion.</p>
 */
@Service
public class QuizService {

    private final QuizRepository quizRepository;

    @Autowired
    public QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    /**
     * Validates answer indices, converts to entity, stamps the creator, and persists.
     *
     * @param quizDto   validated quiz from the request body
     * @param createdBy email of the authenticated user creating the quiz
     * @return the stored DTO with its server-assigned {@code id}
     */
    public QuizDto addQuizToStorage(QuizDto quizDto, String createdBy) {
        Utils.checkAnswerOptions(quizDto);
        Quiz quizEntity = Utils.convertQuizDtoToEntity(quizDto);
        quizEntity.setCreatedBy(createdBy);
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
        List<QuizDto> result = new ArrayList<>();
        for (Quiz each : quizRepository.findAll()) {
            result.add(Utils.convertEntityToQuizDto(each));
        }
        return result;
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
     * Deletes a quiz, enforcing that only its creator may do so.
     *
     * @param id    quiz identifier
     * @param email email of the authenticated user requesting deletion
     * @throws QuizNotFoundException     if no quiz with the given ID exists (404)
     * @throws ResponseStatusException   403 if the user is not the creator
     */
    public void deleteQuiz(int id, String email) {
        Quiz quiz = findById(id);
        if (!quiz.getCreatedBy().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the author of this quiz");
        }
        quizRepository.delete(quiz);
    }

    /**
     * Evaluates the submitted answer set against the stored correct answers.
     *
     * <p>Comparison is set-based: order does not matter.</p>
     *
     * @param id     quiz identifier
     * @param answer set of submitted option indices
     * @return {@link ResultDto#success()} if sets match, {@link ResultDto#wrong()} otherwise
     * @throws QuizNotFoundException if no quiz with the given ID exists
     */
    public ResultDto solveQuizById(int id, Set<Integer> answer) {
        Quiz quiz = findById(id);
        Set<Integer> correct = Utils.getIndexOfAnswer(quiz.getOptions());
        return correct.equals(answer) ? ResultDto.success() : ResultDto.wrong();
    }

    /**
     * Loads a {@link Quiz} entity by ID, throwing 404 if absent.
     *
     * @param id quiz identifier
     * @return the JPA entity
     * @throws QuizNotFoundException if not found
     */
    public Quiz findById(int id) {
        return quizRepository.findById(id)
            .orElseThrow(QuizNotFoundException::new);
    }
}
