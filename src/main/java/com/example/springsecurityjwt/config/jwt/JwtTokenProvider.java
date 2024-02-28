package com.example.springsecurityjwt.config.jwt;

import com.example.springsecurityjwt.model.entity.User;
import com.example.springsecurityjwt.service.UserDetailService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class JwtTokenProvider {
    private final JwtProperties jwtProperties;

    private final UserDetailService userDetailService;

    private final RedisTemplate redisTemplate;

    public final static String TOKEN_PREFIX = "Bearer ";

    public String generateToken(User user, Duration expiredAt){
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()),user);
    }

    private String makeToken(Date expiry, User user){
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setSubject(user.getEmail())
                //TODO claim에 어떤 값을 넣을지 고민해보기. 사용자의 id를 넣는게 맞는지, id는 authentication에서 가져오면 되지 않나
                .claim("id",user.getId())
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

    //TODO 수정
    public boolean validToken(String token){
        try{
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())
                    .parseClaimsJws(token);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    //토큰 기반으로 인증 정보를 가져오는 메서드
    public Authentication getAuthentication(String token) {
        //비밀값으로 토큰을 복호화한 뒤 클레임에 있는 사용자 이메일인 token subject를 가져온다
        Claims claims = getClaims(token);
        UserDetails userDetails = userDetailService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }

//    //토큰 기반으로 유저 ID를 가져오는 메서드
//    public Long getUserId(String token) {
//        Claims claims = getClaims(token);
//        return claims.get("id", Long.class);
//    }

    public Date getExpiration(String token){
        Claims claims = getClaims(token);

        return claims.getExpiration();
    }
    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }

    public String getToken(String authorizationHeader){
        //JWT가 Bearer 시작한다면 Bearer 빼고 return
        if(authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)){
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    public void removeTokenFromRedis(String token){
        if (redisTemplate.opsForValue().get(token) != null){
            // Refresh Token을 삭제
            redisTemplate.delete(token);

        }else{
            throw new IllegalArgumentException("refresh key에 대한 값이 없습니다.");
        }
    }

    public Long getUserIdFromRedis(String token){

        Optional<Long> optionalValue = Optional.ofNullable((Long) redisTemplate.opsForValue().get(token));
        return optionalValue.orElseThrow(() -> new IllegalArgumentException("refresh key에 대한 값이 없습니다."));
    }
}
