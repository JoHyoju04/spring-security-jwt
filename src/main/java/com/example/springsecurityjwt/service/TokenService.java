package com.example.springsecurityjwt.service;

import com.example.springsecurityjwt.config.jwt.JwtTokenProvider;
import com.example.springsecurityjwt.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);

    public String createNewAccessToken(String refreshToken) {
        // 토큰 유효성 검사에 실패하면 예외 발생(만료되었는지, 시그니처 체크)
        if(!jwtTokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected token");
        }

        Long userId = jwtTokenProvider.getUserIdFromRedis(refreshToken);

//        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        User user = userService.findById(userId);

        return jwtTokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
    }
}
