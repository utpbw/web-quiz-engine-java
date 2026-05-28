package engine.controller;

import engine.model.Quiz;
import engine.model.Result;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller exposing the single hardcoded quiz at {@code /api/quiz}.
 *
 * <p>Stage 1 — Solving a simple quiz: one fixed question, answer index 2
 * (zero-based) is the correct option ("Cup of coffee").</p>
 */
@RestController
public class QuizController {

    /** The single quiz served by this stage of the engine. */
    private final Quiz quiz = new Quiz(
            "The Java Logo",
            "What is depicted on the Java logo?",
            new String[]{"Robot", "Tea leaf", "Cup of coffee", "Bug"}
    );

    /**
     * Returns the current quiz question with its answer options.
     *
     * @return the quiz; never {@code null}
     */
    @GetMapping(path = "/api/quiz")
    public Quiz getQuiz() {
        return quiz;
    }

    /**
     * Evaluates the submitted answer index and returns a result.
     *
     * @param answer zero-based index of the chosen option
     * @return {@link Result#success()} if {@code answer == 2}, otherwise {@link Result#wrong()}
     */
    @PostMapping(path = "/api/quiz")
    public Result solveQuiz(@RequestParam int answer) {
        if (answer == 2) {
            return Result.success();
        } else {
            return Result.wrong();
        }
    }
}
