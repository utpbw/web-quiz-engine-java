package engine.controller;

import engine.model.Quiz;
import engine.model.Result;
import engine.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller for the quiz engine API.
 *
 * <p>Stage 3 — Making quizzes more interesting: creation now requires
 * non-blank title/text and at least 2 options (validated via {@code @Valid}).
 * The answer field is an array of indices; the solve endpoint accepts a JSON
 * body instead of a query parameter.</p>
 */
@RestController
public class QuizController {

    private final QuizService service;

    /** @param service in-memory quiz store, injected by Spring */
    @Autowired
    public QuizController(QuizService service) {
        this.service = service;
    }

    /**
     * Creates a new quiz after validating the request body.
     * Returns 400 if title/text are blank or options has fewer than 2 entries.
     *
     * @param quiz validated quiz from the JSON request body
     * @return the stored quiz with its generated {@code id}
     */
    @PostMapping(value = "/api/quizzes", consumes = "application/json")
    public Quiz createQuiz(@Valid @NotNull @RequestBody Quiz quiz) {
        return service.addQuizToStorage(quiz);
    }

    /**
     * Returns a single quiz by ID without the answer field.
     *
     * @param id quiz identifier
     * @return the matching quiz
     */
    @GetMapping(path = "/api/quizzes/{id}")
    public Quiz getQuiz(@PathVariable int id) {
        return service.getQuizById(id);
    }

    /**
     * Returns all quizzes currently stored; empty array when none exist.
     *
     * @return list of all quizzes
     */
    @GetMapping(path = "/api/quizzes")
    public List<Quiz> getAllQuizzes() {
        return service.getAllQuizzesFromStorage();
    }

    /**
     * Evaluates the submitted answer set for the given quiz.
     * The request body must be {@code {"answer": [index, ...]}}; send {@code []}
     * when guessing that no options are correct.
     *
     * @param id     quiz identifier
     * @param answer Quiz object whose {@code answer} set carries the submitted indices
     * @return success/failure result with feedback message
     */
    @PostMapping(path = "/api/quizzes/{id}/solve", produces = APPLICATION_JSON_VALUE)
    public Result solveQuiz(@PathVariable int id, @RequestBody Quiz answer) {
        return service.solveQuizById(id, answer.getAnswer());
    }
}
