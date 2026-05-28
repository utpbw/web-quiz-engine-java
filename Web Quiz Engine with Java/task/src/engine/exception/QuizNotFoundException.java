package engine.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a quiz ID is not found in the store.
 *
 * <p>{@code @ResponseStatus} maps this exception to HTTP 404 automatically,
 * so no {@code @ExceptionHandler} is needed.</p>
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Quiz not found")
public class QuizNotFoundException extends RuntimeException {}

