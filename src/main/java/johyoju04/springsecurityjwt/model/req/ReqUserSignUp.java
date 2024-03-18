package johyoju04.springsecurityjwt.model.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import static johyoju04.springsecurityjwt.config.common.Constants.PASSWORD;

@Getter
@Setter
public class ReqUserSignUp {
    @Email
    private String email;

    @NotBlank
    @JsonProperty(PASSWORD)
    private String password;
}
