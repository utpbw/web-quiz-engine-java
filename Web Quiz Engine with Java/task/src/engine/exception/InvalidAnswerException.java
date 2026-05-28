package engine.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a submitted answer index falls outside the valid option range.
 *
 * <p>{@code @ResponseStatus} maps this exception to HTTP 400 automatically.</p>
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid Answer Option")
public class InvalidAnswerException extends RuntimeException {}

