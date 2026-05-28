package engine.controller;

import engine.model.dto.QuizDto;
import engine.model.dto.RegisterDto;
import engine.model.dto.ResultDto;
import engine.service.QuizService;
import engine.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * REST controller for the quiz engine API.
 *
 * <p>Stage 5 — User authorization:
 * <ul>
 *   <li>All quiz operations require HTTP Basic Auth (401 otherwise).</li>
 *   <li>{@code POST /api/register} is public.</li>
 *   <li>{@code DELETE /api/quizzes/{id}} returns 204 for the owner, 403 for others, 404 if missing.</li>
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
     * Returns 400 if the request body fails validation.
     *
     * @param quizDto   validated quiz from the JSON request body
     * @param principal injected by Spring Security from the Basic Auth credentials
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
     * Returns all quizzes currently stored; empty array when none exist.
     *
     * @return list of all quiz DTOs
     */
    @GetMapping("/api/quizzes")
    public List<QuizDto> getAllQuizzes() {
        return quizService.getAllQuizzesFromStorage();
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
     * Evaluates the submitted answer set for the given quiz.
     * Send {@code {"answer": []}} when guessing that no option is correct.
     *
     * @param id     quiz identifier
     * @param answer QuizDto whose {@code answer} set carries the submitted indices
     * @return success/failure result DTO with feedback
     */
    @PostMapping("/api/quizzes/{id}/solve")
    public ResultDto solveQuiz(@PathVariable int id, @RequestBody QuizDto answer) {
        return quizService.solveQuizById(id, answer.getAnswer());
    }
}
