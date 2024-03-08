package johyoju04.springsecurityjwt.config;

import johyoju04.springsecurityjwt.config.jwt.JwtTokenProvider;
import johyoju04.springsecurityjwt.security.AccessTokenAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers("/img/**", "/css/**", "/js/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, final JwtTokenProvider jwtTokenProvider, final RedisTemplate redisTemplate) throws Exception {

        http.csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .logout().disable();

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        AuthenticationManagerBuilder sharedObject = http.getSharedObject(AuthenticationManagerBuilder.class);
        sharedObject.authenticationProvider(new AccessTokenAuthenticationProvider(jwtTokenProvider, redisTemplate));
        AuthenticationManager authenticationManager = sharedObject.build();
        http.authenticationManager(authenticationManager);

        http.addFilterBefore(tokenAuthenticationFilter(jwtTokenProvider, authenticationManager), UsernamePasswordAuthenticationFilter.class);

        http.authorizeRequests()
                .requestMatchers("/api/token", "/api/login", "/api/signup").permitAll()
                .requestMatchers("/**").authenticated()
                .anyRequest().permitAll();

//        http.oauth2Login()
//                .loginPage("/login")
//                .authorizationEndpoint()
//                .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
//                .and()
//                .successHandler(oAuth2SuccessHandler())
//                .userInfoEndpoint()
//                .userService(oAuth2UserCustomService);


        http.exceptionHandling()
                .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        new AntPathRequestMatcher("/**"));

        return http.build();
    }


//    @Bean
//    public OAuth2SuccessHandler oAuth2SuccessHandler() {
//        return new OAuth2SuccessHandler(tokenProvider,
//                refreshTokenRepository,
//                oAuth2AuthorizationRequestBasedOnCookieRepository(),
//                userService
//        );
//    }

    private TokenAuthenticationFilter tokenAuthenticationFilter(final JwtTokenProvider jwtTokenProvider, final AuthenticationManager authenticationManager) {
        return new TokenAuthenticationFilter(jwtTokenProvider, authenticationManager);
    }

//    @Bean
//    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
//        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
//    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}