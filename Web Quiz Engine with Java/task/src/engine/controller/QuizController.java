package engine.controller;

import engine.model.Quiz;
import engine.model.Result;
import engine.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for the quiz engine API.
 *
 * <p>Stage 2 — Lots of quizzes: quizzes are stored in memory and identified
 * by an auto-incremented integer ID.</p>
 */
@RestController
public class QuizController {

    private final QuizService service;

    /**
     * @param service in-memory quiz store, injected by Spring
     */
    @Autowired
    public QuizController(QuizService service) {
        this.service = service;
    }

    /**
     * Creates a new quiz from the JSON request body.
     *
     * @param quiz deserialized quiz (answer field accepted but not returned)
     * @return the stored quiz with its generated {@code id}
     */
    @PostMapping(value = "/api/quizzes", consumes = "application/json")
    public Quiz createQuiz(@RequestBody Quiz quiz) {
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
     * Evaluates the submitted answer for the given quiz.
     *
     * @param id     quiz identifier
     * @param answer zero-based index of the chosen option (request param)
     * @return success/failure result with feedback message
     */
    @PostMapping(path = "/api/quizzes/{id}/solve")
    public Result solveQuiz(@PathVariable int id, @RequestParam int answer) {
        return service.solveQuizById(id, answer);
    }
}
