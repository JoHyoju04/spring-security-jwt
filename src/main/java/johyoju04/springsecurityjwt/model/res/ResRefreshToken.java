package johyoju04.springsecurityjwt.model.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResRefreshToken {
    private String refreshToken;
    private String tokenType;
}
