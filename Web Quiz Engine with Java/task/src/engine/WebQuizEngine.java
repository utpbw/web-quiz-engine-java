package engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Web Quiz Engine Spring Boot application.
 *
 * <p>Starts an embedded Tomcat server and registers all Spring-managed
 * components found under the {@code engine} package.</p>
 */
@SpringBootApplication
public class WebQuizEngine {

    /**
     * Bootstraps the Spring application context and starts the web server.
     *
     * @param args command-line arguments passed to the JVM
     */
    public static void main(String[] args) {
        SpringApplication.run(WebQuizEngine.class, args);
    }

}
