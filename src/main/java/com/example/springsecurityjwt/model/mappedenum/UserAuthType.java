package com.example.springsecurityjwt.model.mappedenum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserAuthType {
    EMAIL(1);
//    KAKAO(2),
//    APPLE(3),
//    NAVER(4),
//    GOOGLE(5);

    @Getter
    private final int type;

    public static UserAuthType valueOf(int type) {
        for (UserAuthType userAuthType : values())
            if (userAuthType.getType() == type)
                return userAuthType;

        return null;
    }

    public boolean isSocial() {
        return this != EMAIL;
    }
}