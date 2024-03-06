package johyoju04.springsecurityjwt.model.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUserSignUp {
    @Email
    private String email;

    @NotBlank
    private String password;
}
