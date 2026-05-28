package engine.model.jpa;

import jakarta.persistence.*;

/**
 * JPA entity representing a registered user.
 *
 * <p>Email is the unique login identifier.
 * Passwords are stored BCrypt-hashed; never in plain text.</p>
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    /** Unique login; used as the Spring Security username. */
    @Column(unique = true, nullable = false)
    private String email;

    /** BCrypt-hashed password. */
    @Column(nullable = false)
    private String password;

    public User() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
