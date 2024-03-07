package johyoju04.springsecurityjwt.model.res;

import lombok.Getter;

@Getter
public class ResToken {
    private final String accessToken;
    private final String refreshToken;
    private final String tokenType;

    private ResToken(String accessToken, String refreshToken, String tokenType){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
    }

    public static ResToken of(String accessToken, String refreshToken, String tokenType){
        return new ResToken(accessToken,refreshToken,tokenType);
    }
}
