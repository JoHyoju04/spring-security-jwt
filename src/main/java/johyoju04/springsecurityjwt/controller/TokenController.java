package johyoju04.springsecurityjwt.controller;

import johyoju04.springsecurityjwt.model.req.ReqCreateAccessToken;
import johyoju04.springsecurityjwt.model.res.ResAccessToken;
import johyoju04.springsecurityjwt.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TokenController {
    private final TokenService tokenService;

    @PostMapping("/token")
    public ResponseEntity<ResAccessToken> createNewAccessToken(
            @RequestBody @Valid ReqCreateAccessToken req
    ) {
        String newAccessToken = tokenService.createNewAccessToken(req.getRefreshToken());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResAccessToken(newAccessToken));
    }

}