package engine.repository;

import engine.model.jpa.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data repository for {@link User} entities.
 *
 * <p>The custom {@link #findByEmail(String)} method is used by Spring Security's
 * {@code UserDetailsService} to look up users by their login email.</p>
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Looks up a user by their unique email address.
     *
     * @param email the login email
     * @return the matching user, or empty if none registered with that email
     */
    Optional<User> findByEmail(String email);
}
