package johyoju04.springsecurityjwt.model.res;

import lombok.Getter;

@Getter
public class ResAccessToken {
    private final String accessToken;
    private final String tokenType;

    private ResAccessToken(String accessToken,String tokenType){
        this.accessToken = accessToken;
        this.tokenType = tokenType;
    }

    public static ResAccessToken of(String accessToken, String tokenType){
        return new ResAccessToken(accessToken, tokenType);
    }

}
