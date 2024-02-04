package com.example.springsecurityjwt.model.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCreateAccessToken {
    private String refreshToken;
}
