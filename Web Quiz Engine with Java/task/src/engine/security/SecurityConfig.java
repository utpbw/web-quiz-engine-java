package engine.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration for the quiz engine.
 *
 * <p>Rules:
 * <ul>
 *   <li>{@code POST /api/register} — public, no auth required</li>
 *   <li>{@code POST /actuator/shutdown} — public (required by Hyperskill tests)</li>
 *   <li>All other requests — HTTP Basic Auth with a registered user's email/password</li>
 * </ul>
 * Sessions are stateless; every request must carry credentials.
 *
 * <p>No explicit {@code DaoAuthenticationProvider} is wired here. Spring Security
 * auto-discovers the {@link engine.service.UserService} ({@code UserDetailsService})
 * and the {@link PasswordEncoder} beans from the context, which avoids the
 * circular dependency that would arise from injecting {@code UserService} here
 * while it itself depends on {@link PasswordEncoder}.</p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * BCrypt password encoder shared with {@link engine.service.UserService}.
     * Defined here so it is available before {@code UserService} is created.
     *
     * @return a {@link BCryptPasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain.
     *
     * @param http the {@link HttpSecurity} builder
     * @return the built {@link SecurityFilterChain}
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .headers(h -> h.frameOptions(f -> f.disable()))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/register", "/error").permitAll()
                .requestMatchers(HttpMethod.POST, "/actuator/shutdown").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
