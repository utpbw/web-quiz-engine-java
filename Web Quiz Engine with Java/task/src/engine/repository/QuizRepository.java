package engine.repository;

import engine.model.jpa.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link Quiz} entities.
 *
 * <p>Extends {@link JpaRepository} so {@code findAll()} returns a {@link java.util.List}
 * directly rather than the {@code Iterable} of {@code CrudRepository}.</p>
 */
public interface QuizRepository extends JpaRepository<Quiz, Integer> {
}
