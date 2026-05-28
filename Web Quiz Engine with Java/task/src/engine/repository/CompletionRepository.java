package engine.repository;

import engine.model.jpa.QuizCompletion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link QuizCompletion} entities.
 *
 * <p>The derived query {@link #findByUserEmailOrderByCompletedAtDesc} returns
 * all completions for a given user, newest first, as a pageable result.</p>
 */
public interface CompletionRepository extends JpaRepository<QuizCompletion, Long> {

    /**
     * Returns a page of completions for the given user, sorted newest-first.
     *
     * @param userEmail the authenticated user's email
     * @param pageable  page number and size
     * @return paged completions ordered by {@code completedAt} descending
     */
    Page<QuizCompletion> findByUserEmailOrderByCompletedAtDesc(String userEmail, Pageable pageable);
}
