package johyoju04.springsecurityjwt.config;

import johyoju04.springsecurityjwt.config.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    private final RedisTemplate redisTemplate;

    //요청이 오면 헤더값을 비교해서 토큰유무 및 유효성 체크
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        //요청 헤더의 Authorization 키의 값 조회
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = jwtTokenProvider.getToken(authorizationHeader);

        //토큰 유효성 검사
        if(StringUtils.hasText(token) && jwtTokenProvider.validToken(token)){
            //Redis에 해당 accessToken logout 여부 확인(블랙 리스트)
            String isLogout = (String) redisTemplate.opsForValue().get(token);

            //로그아웃 하지 않은 경우 (블랙리스에 없음)
            if(ObjectUtils.isEmpty(isLogout)){
                //토큰으로부터 유저 정보 가져오기
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                // SecurityContext 에 Authentication 객체를 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

//    private String getAccessToken(String authorizationHeader){
//        //JWT가 Bearer 시작한다면 Bearer 빼고 return
//        if(authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)){
//            return authorizationHeader.substring(TOKEN_PREFIX.length());
//        }
//        return null;
//    }

}
