package johyoju04.springsecurityjwt.controller;

import johyoju04.springsecurityjwt.config.jwt.JwtProperties;
import johyoju04.springsecurityjwt.model.req.ReqDeleteRefreshToken;
import johyoju04.springsecurityjwt.model.req.ReqUserSignUp;
import johyoju04.springsecurityjwt.service.UserService;
import johyoju04.springsecurityjwt.config.jwt.JwtTokenProvider;
import johyoju04.springsecurityjwt.model.entity.User;
import johyoju04.springsecurityjwt.model.req.ReqUserSignIn;
import johyoju04.springsecurityjwt.model.res.ResToken;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;
    private final JwtProperties jwtProperties;

    @PostMapping("/signup")
    public ResponseEntity<Object> signUp(@RequestBody @Valid ReqUserSignUp request){
        userService.save(request);

        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResToken> signIn(@RequestBody @Valid ReqUserSignIn req){
        //이메일과 비밀번호로 user 조회
        User user = userService.signIn(req);

        //access token 발급
        String accessToken = jwtTokenProvider.generateToken(user, jwtProperties.getAccessTokenExpiration());

        //refresh token 발급
        String refreshToken = jwtTokenProvider.generateToken(user, jwtProperties.getRefreshTokenExpiration());

        //redis에 RT:test@gmail.com(key) / 23jijiofj2io3hi32hiongiodsninioda(value) 형태로 리프레시 토큰 저장하기
        //REFRESH_TOKEN_DURATION 초 후에 만료된다(상대적인 시간)
        redisTemplate.opsForValue().set(refreshToken, String.valueOf(user.getId()), (int) jwtProperties.getAccessTokenExpiration().toSeconds(), TimeUnit.SECONDS);

        ResToken resToken = new ResToken(accessToken,refreshToken,"Bearer");

        // JWT 토큰을 응답 바디에 넣어서 클라이언트에게 전달
        return ResponseEntity.ok()
                .body(resToken);
    }

    @PostMapping(value = "/logout" )
    public ResponseEntity<Object> logout(@RequestHeader("Authorization") String header, @RequestBody @Valid ReqDeleteRefreshToken req){

        String refreshToken = req.getRefreshToken();

        // Redis에서 해당 User email로 저장된 Refresh Token 이 있는지 여부를 확인 후에 있을 경우 삭제를 한다.
        jwtTokenProvider.removeTokenFromRedis(refreshToken);

        // SecurityContext 초기화하여 현재 세션 무효화
        SecurityContextHolder.clearContext();

        String accessToken = jwtTokenProvider.getToken(header);

        // 해당 Access Token 유효시간을 가지고 와서 BlackList에 저장하기
        //key,value,timeout,timeunit
        redisTemplate.opsForValue().set(accessToken,"logout",(int) jwtProperties.getAccessTokenExpiration().toSeconds(),TimeUnit.SECONDS);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "")
                .build();
    }

    @GetMapping("/users")
    public ResponseEntity<User> getUser(Authentication authentication){

        String email = authentication.getName();

        return new ResponseEntity<>(userService.findByEmail(email),HttpStatus.OK);
    }

}
