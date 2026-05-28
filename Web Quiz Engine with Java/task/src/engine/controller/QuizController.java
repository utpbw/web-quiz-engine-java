package engine.controller;

import engine.model.dto.QuizDto;
import engine.model.dto.ResultDto;
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
 * <p>Stage 4 — Moving quizzes to DB: the API is unchanged from stage 3;
 * quizzes are now persisted to an H2 file database and survive server restarts.</p>
 */
@RestController
public class QuizController {

    private final QuizService service;

    /** @param service quiz service with H2-backed persistence, injected by Spring */
    @Autowired
    public QuizController(QuizService service) {
        this.service = service;
    }

    /**
     * Creates a new quiz after validating the request body.
     * Returns 400 if title/text are blank or options has fewer than 2 entries.
     *
     * @param quizDto validated quiz from the JSON request body
     * @return the stored quiz DTO with its generated {@code id}
     */
    @PostMapping(value = "/api/quizzes", consumes = "application/json")
    public QuizDto createQuiz(@Valid @NotNull @RequestBody QuizDto quizDto) {
        return service.addQuizToStorage(quizDto);
    }

    /**
     * Returns a single quiz by ID without the answer field.
     *
     * @param id quiz identifier
     * @return the matching quiz DTO
     */
    @GetMapping(path = "/api/quizzes/{id}")
    public QuizDto getQuiz(@PathVariable int id) {
        return service.getQuizById(id);
    }

    /**
     * Returns all quizzes currently stored; empty array when none exist.
     *
     * @return list of all quiz DTOs
     */
    @GetMapping(path = "/api/quizzes")
    public List<QuizDto> getAllQuizzes() {
        return service.getAllQuizzesFromStorage();
    }

    /**
     * Evaluates the submitted answer set for the given quiz.
     * The request body must be {@code {"answer": [index, ...]}}; send {@code []}
     * when guessing that no options are correct.
     *
     * @param id     quiz identifier
     * @param answer QuizDto whose {@code answer} set carries the submitted indices
     * @return success/failure result DTO with feedback message
     */
    @PostMapping(path = "/api/quizzes/{id}/solve", produces = APPLICATION_JSON_VALUE)
    public ResultDto solveQuiz(@PathVariable int id, @RequestBody QuizDto answer) {
        return service.solveQuizById(id, answer.getAnswer());
    }
}
