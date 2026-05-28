package engine.service;

import engine.model.dto.RegisterDto;
import engine.model.jpa.User;
import engine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Implements Spring Security's {@link UserDetailsService} so HTTP Basic Auth can look
 * up users by email, and exposes a {@link #register(RegisterDto)} method for
 * the registration endpoint.
 *
 * <p>{@link PasswordEncoder} is field-injected after construction so that Spring
 * can create this bean before the {@link engine.security.SecurityConfig} defines the
 * encoder bean — no circular dependency.</p>
 */
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Called by Spring Security on every authenticated request to load the user record.
     *
     * @param email the login email supplied via HTTP Basic Auth
     * @return a Spring Security {@link UserDetails} wrapping the stored credentials
     * @throws UsernameNotFoundException if no user with that email exists
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(), user.getPassword(), List.of()
        );
    }

    /**
     * Registers a new user after validating that the email is not already taken.
     *
     * @param dto registration request with email and plain-text password
     * @throws ResponseStatusException 400 if the email is already registered
     */
    public void register(RegisterDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already taken");
        }
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
    }
}
