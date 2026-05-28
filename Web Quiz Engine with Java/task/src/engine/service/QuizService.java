package engine.service;

import engine.exception.QuizNotFoundException;
import engine.model.dto.CompletionDto;
import engine.model.dto.QuizDto;
import engine.model.dto.ResultDto;
import engine.model.jpa.Quiz;
import engine.model.jpa.QuizCompletion;
import engine.repository.CompletionRepository;
import engine.repository.QuizRepository;
import engine.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Service layer bridging the REST API and the JPA repositories.
 *
 * <p>Converts between DTOs (API contract) and JPA entities, delegates
 * persistence to {@link QuizRepository} and {@link CompletionRepository},
 * and enforces quiz ownership on deletion.</p>
 */
@Service
public class QuizService {

    private static final int PAGE_SIZE = 10;

    private final QuizRepository quizRepository;
    private final CompletionRepository completionRepository;

    @Autowired
    public QuizService(QuizRepository quizRepository, CompletionRepository completionRepository) {
        this.quizRepository = quizRepository;
        this.completionRepository = completionRepository;
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
     * Returns a page of all stored quizzes (10 per page).
     *
     * @param page zero-based page number
     * @return a Spring Data {@link Page} of quiz DTOs
     */
    public Page<QuizDto> getAllQuizzesFromStorage(int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        return quizRepository.findAll(pageable)
            .map(Utils::convertEntityToQuizDto);
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
     * @throws QuizNotFoundException   if no quiz with the given ID exists (404)
     * @throws ResponseStatusException 403 if the user is not the creator
     */
    public void deleteQuiz(int id, String email) {
        Quiz quiz = findById(id);
        if (!quiz.getCreatedBy().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the author of this quiz");
        }
        quizRepository.delete(quiz);
    }

    /**
     * Evaluates the submitted answer set. If correct, records a completion.
     *
     * @param id        quiz identifier
     * @param answer    set of submitted option indices
     * @param userEmail email of the authenticated user
     * @return {@link ResultDto#success()} if correct, {@link ResultDto#wrong()} otherwise
     * @throws QuizNotFoundException if no quiz with the given ID exists
     */
    public ResultDto solveQuizById(int id, Set<Integer> answer, String userEmail) {
        Quiz quiz = findById(id);
        Set<Integer> correct = Utils.getIndexOfAnswer(quiz.getOptions());
        boolean success = correct.equals(answer);
        if (success) {
            QuizCompletion completion = new QuizCompletion();
            completion.setUserEmail(userEmail);
            completion.setQuizId(id);
            completion.setCompletedAt(LocalDateTime.now());
            completionRepository.save(completion);
        }
        return success ? ResultDto.success() : ResultDto.wrong();
    }

    /**
     * Returns a page of quiz completions for the given user, newest first (10 per page).
     *
     * @param userEmail authenticated user's email
     * @param page      zero-based page number
     * @return a Spring Data {@link Page} of completion DTOs
     */
    public Page<CompletionDto> getCompletedQuizzes(String userEmail, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        return completionRepository
            .findByUserEmailOrderByCompletedAtDesc(userEmail, pageable)
            .map(c -> new CompletionDto(c.getQuizId(), c.getCompletedAt()));
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
