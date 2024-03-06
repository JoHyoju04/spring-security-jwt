package johyoju04.springsecurityjwt.model.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCreateAccessToken {
    @NotBlank
    private String refreshToken;
}
