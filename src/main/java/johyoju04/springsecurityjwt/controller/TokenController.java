package johyoju04.springsecurityjwt.controller;

import jakarta.validation.Valid;
import johyoju04.springsecurityjwt.model.req.ReqCreateAccessToken;
import johyoju04.springsecurityjwt.model.res.ResAccessToken;
import johyoju04.springsecurityjwt.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class TokenController {
    private final TokenService tokenService;

    @PostMapping("/token")
    public ResAccessToken createNewAccessToken(
            @RequestBody @Valid ReqCreateAccessToken req
    ) {
        String newAccessToken = tokenService.createNewAccessToken(req);

        return ResAccessToken.of(newAccessToken, "Bearer");
    }

}