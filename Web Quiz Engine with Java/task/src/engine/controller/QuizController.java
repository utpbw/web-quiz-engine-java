package engine.controller;

import engine.model.dto.CompletionDto;
import engine.model.dto.QuizDto;
import engine.model.dto.RegisterDto;
import engine.model.dto.ResultDto;
import engine.service.QuizService;
import engine.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * REST controller for the quiz engine API.
 *
 * <p>Stage 6 — Advanced queries:
 * <ul>
 *   <li>{@code GET /api/quizzes?page=n} returns a paginated {@link Page} of quizzes (10 per page).</li>
 *   <li>{@code GET /api/quizzes/completed?page=n} returns the authenticated user's completions,
 *       newest first, as a paginated {@link Page}.</li>
 *   <li>Correct quiz answers now record a {@link engine.model.jpa.QuizCompletion}.</li>
 * </ul>
 * </p>
 */
@RestController
public class QuizController {

    private final QuizService quizService;
    private final UserService userService;

    @Autowired
    public QuizController(QuizService quizService, UserService userService) {
        this.quizService = quizService;
        this.userService = userService;
    }

    // ── Registration ──────────────────────────────────────────────────────────

    /**
     * Registers a new user. Returns 400 if the email is already taken or the
     * email/password format is invalid.
     *
     * @param dto registration request body with email and password
     */
    @PostMapping("/api/register")
    public void register(@Valid @RequestBody RegisterDto dto) {
        userService.register(dto);
    }

    // ── Quiz CRUD ─────────────────────────────────────────────────────────────

    /**
     * Creates a new quiz owned by the currently authenticated user.
     *
     * @param quizDto   validated quiz from the JSON request body
     * @param principal injected by Spring Security
     * @return the stored quiz DTO with its generated {@code id}
     */
    @PostMapping(value = "/api/quizzes", consumes = "application/json")
    public QuizDto createQuiz(@Valid @NotNull @RequestBody QuizDto quizDto, Principal principal) {
        return quizService.addQuizToStorage(quizDto, principal.getName());
    }

    /**
     * Returns a single quiz by ID without the answer field.
     *
     * @param id quiz identifier
     * @return the matching quiz DTO
     */
    @GetMapping("/api/quizzes/{id}")
    public QuizDto getQuiz(@PathVariable int id) {
        return quizService.getQuizById(id);
    }

    /**
     * Returns a page of all quizzes (10 per page).
     *
     * @param page zero-based page number (default 0)
     * @return a Spring Data page of quiz DTOs
     */
    @GetMapping("/api/quizzes")
    public Page<QuizDto> getAllQuizzes(@RequestParam(defaultValue = "0") int page) {
        return quizService.getAllQuizzesFromStorage(page);
    }

    /**
     * Returns a page of the authenticated user's successful completions, newest first.
     *
     * @param page      zero-based page number (default 0)
     * @param principal injected by Spring Security
     * @return a Spring Data page of completion DTOs
     */
    @GetMapping("/api/quizzes/completed")
    public Page<CompletionDto> getCompletedQuizzes(
            @RequestParam(defaultValue = "0") int page,
            Principal principal) {
        return quizService.getCompletedQuizzes(principal.getName(), page);
    }

    /**
     * Deletes a quiz created by the authenticated user.
     * Returns 204 on success, 404 if not found, 403 if not the owner.
     *
     * @param id        quiz identifier
     * @param principal injected by Spring Security
     */
    @DeleteMapping("/api/quizzes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuiz(@PathVariable int id, Principal principal) {
        quizService.deleteQuiz(id, principal.getName());
    }

    // ── Solve ─────────────────────────────────────────────────────────────────

    /**
     * Evaluates the submitted answer set. Records a completion if correct.
     *
     * @param id        quiz identifier
     * @param answer    QuizDto whose {@code answer} set carries the submitted indices
     * @param principal injected by Spring Security
     * @return success/failure result DTO with feedback
     */
    @PostMapping("/api/quizzes/{id}/solve")
    public ResultDto solveQuiz(@PathVariable int id,
                               @RequestBody QuizDto answer,
                               Principal principal) {
        return quizService.solveQuizById(id, answer.getAnswer(), principal.getName());
    }
}
