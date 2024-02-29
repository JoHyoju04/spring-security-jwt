package com.example.springsecurityjwt.model.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqDeleteRefreshToken {
    @NotBlank
    private String refreshToken;
}
