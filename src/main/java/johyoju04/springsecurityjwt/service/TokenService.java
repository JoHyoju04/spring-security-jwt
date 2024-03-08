package johyoju04.springsecurityjwt.service;

import johyoju04.springsecurityjwt.config.jwt.JwtTokenProvider;
import johyoju04.springsecurityjwt.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final RedisTemplate redisTemplate;

    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);

    public String createNewAccessToken(String refreshToken) {
        // 토큰 유효성 검사에 실패하면 예외 발생(만료되었는지, 시그니처 체크)
        if(StringUtils.hasText(refreshToken) && !jwtTokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected token");
        }

        //Redis에 해당 accessToken logout 여부 확인(블랙 리스트)
        //String isLogout = (String) redisTemplate.opsForValue().get(accessToken);

        //TODO 토큰에서 조회하기
        Long userId = jwtTokenProvider.getUserIdFromRedis(refreshToken);

//        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        User user = userService.findById(userId);

        return jwtTokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
    }
}
