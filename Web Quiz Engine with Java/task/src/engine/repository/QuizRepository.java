package engine.repository;

import engine.model.jpa.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link Quiz} entities.
 *
 * <p>Extends {@link JpaRepository} to get {@code findAll()} returning a {@link java.util.List}
 * directly, avoiding the {@code Iterable} wrapping of {@code CrudRepository}.</p>
 */
public interface QuizRepository extends JpaRepository<Quiz, Integer> {
}
