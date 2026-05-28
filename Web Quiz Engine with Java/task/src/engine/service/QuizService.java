package engine.service;

import engine.exception.QuizNotFoundException;
import engine.model.Quiz;
import engine.model.Result;
import engine.utils.Utils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-memory store for quiz instances.
 *
 * <p>Thread-safe: uses {@link ConcurrentHashMap} and an {@link AtomicInteger}
 * ID counter so concurrent requests never collide.</p>
 */
@Service
public class QuizService {

    private final Map<Integer, Quiz> storage = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger();

    /**
     * Validates answer indices, assigns an ID, and persists the quiz.
     *
     * @param quiz the validated quiz to store
     * @return the same quiz with its ID populated
     */
    public Quiz addQuizToStorage(Quiz quiz) {
        Utils.checkAnswerOptions(quiz);
        int id = idGenerator.incrementAndGet();
        quiz.setId(id);
        storage.put(id, quiz);
        return quiz;
    }

    /**
     * Returns all quizzes currently in the store.
     *
     * @return snapshot list; empty when no quizzes have been created
     */
    public List<Quiz> getAllQuizzesFromStorage() {
        return new ArrayList<>(storage.values());
    }

    /**
     * Retrieves a quiz by its ID.
     *
     * @param id quiz identifier
     * @return the matching quiz
     * @throws QuizNotFoundException if no quiz with the given ID exists
     */
    public Quiz getQuizById(int id) {
        Quiz quiz = storage.get(id);
        if (quiz == null) {
            throw new QuizNotFoundException();
        }
        return quiz;
    }

    /**
     * Evaluates the submitted answer set for the specified quiz.
     *
     * <p>Comparison is set-based: order does not matter.</p>
     *
     * @param id     quiz identifier
     * @param answer set of submitted option indices
     * @return {@link Result#success()} if the sets match, {@link Result#wrong()} otherwise
     * @throws QuizNotFoundException if no quiz with the given ID exists
     */
    public Result solveQuizById(int id, Set<Integer> answer) {
        Quiz quiz = getQuizById(id);
        return quiz.getAnswer().equals(answer) ? Result.success() : Result.wrong();
    }
}
