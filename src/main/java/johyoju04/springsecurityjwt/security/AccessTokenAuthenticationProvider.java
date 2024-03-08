package johyoju04.springsecurityjwt.security;

import johyoju04.springsecurityjwt.config.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@RequiredArgsConstructor
public class AccessTokenAuthenticationProvider implements AuthenticationProvider {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        AccessTokenAuthentication token = (AccessTokenAuthentication) authentication;
        String accessToken = token.getAccessToken();

        //토큰 유효성 검사
        if (!jwtTokenProvider.validToken(accessToken)) {
            return token;
        }

        Long userId = jwtTokenProvider.getUserId(accessToken);
        if (userId == null) {
            return token;
        }

        //Redis에 해당 accessToken logout 여부 확인(블랙 리스트)
        boolean isLogout = Boolean.TRUE.equals(redisTemplate.hasKey(accessToken));
        if (isLogout) {
            return token;
        }

        token.setUserId(userId);
        token.setAuthenticated(true);
        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(AccessTokenAuthentication.class);
    }
}
