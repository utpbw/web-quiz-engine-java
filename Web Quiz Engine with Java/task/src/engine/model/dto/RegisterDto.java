package engine.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request body for {@code POST /api/register}.
 *
 * <p>Validation rules: email must have a valid format; password must be at least 5 characters.</p>
 */
public class RegisterDto {

    /**
     * Must be a valid email address: contains {@code @} and a dot after it.
     * The regexp adds the dot-in-domain requirement that plain {@code @Email} doesn't enforce.
     */
    @Email(regexp = ".+@.+\\..+", message = "must be a valid email address")
    @NotBlank
    private String email;

    /** Must be at least 5 characters long. */
    @Size(min = 5)
    @NotBlank
    private String password;

    public RegisterDto() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
