# spring-security-jwt  
## 개발환경 라이브러리
💡 해당 환경설정을 구성하는 데 사용한 라이브러리 및 프레임워크입니다.

| 개발 환경 | 버전 |
| :------------: | :-------------: | 
| JAVA | 17  |
| Spring Boot | 3.0.2  |
| spring-boot-starter-security | 3.0.2  |
| spring-boot-starter-data-jpa | 3.0.2  |
| spring-boot-starter-data-redis | 3.0.2  |
| io.jsonwebtoken:jjwt | 0.9.1  |
| 빌드관리도구 | Gradle 8.5  |

## 시퀀스 다이어그램
- 회원가입 POST /api/signup
<img width="846" alt="시퀀스 다이어그램-회원가입" src="https://github.com/JoHyoju04/spring-security-jwt/assets/47858282/0c176c7f-34b7-4e00-87c3-025dbf309407">
<br/>

--- 

- 로그인 POST /api/login
<img width="846" alt="시퀀스 다이어그램-로그인" src="https://github.com/JoHyoju04/spring-security-jwt/assets/47858282/b730c868-9117-4d81-a70b-1991ee8047b5">
<br/>

---

- 토큰 재발급 (요청 실패시) POST /api/token
<img width="846" alt="시퀀스 다이어그램-토큰 재발급" src="https://github.com/JoHyoju04/spring-security-jwt/assets/47858282/6e72eb10-1745-4219-b9f4-f62a2a8a39e1">

---

## 프로젝트 구성
<img width="350" alt="프로젝트-구성" src="https://github.com/JoHyoju04/spring-security-jwt/assets/47858282/f5a1ef31-058d-458b-95fb-149e32fab5f6">

## 회원가입
<img width="846" alt="회원가입" src="https://github.com/JoHyoju04/spring-security-jwt/assets/47858282/f4daf825-4b7b-4532-96da-43375a8bb784">

## 로그인 - AccessToken RefreshToken 응답
<img width="846" alt="로그인" src="https://github.com/JoHyoju04/spring-security-jwt/assets/47858282/4879beda-e28c-4771-94ac-5f3f9f5c1067">

## Authorization 유효성 확인
<img width="846" alt="Authorization 유효성 확인" src="https://github.com/JoHyoju04/spring-security-jwt/assets/47858282/a8e1108a-951a-46eb-8a10-65fdf5655842">

## 토근 재발급 - RefreshToken을 이용한 재발급
<img width="846" alt="토큰 재발급" src="https://github.com/JoHyoju04/spring-security-jwt/assets/47858282/28258672-512a-4ff2-b1ab-8e8ec021f305">

## 로그아웃
<img width="846" alt="로그아웃" src="https://github.com/JoHyoju04/spring-security-jwt/assets/47858282/a33a81c7-48a6-47d1-b0e5-043c3d79b565">


## 정리 블로그
[JWT 인증/인가 적용기(1) - Spring Security 인증 구조](https://hohodu.tistory.com/155?category=1150624)  
[JWT 인증/인가 적용기(2) - Spring Security를 통한 JWT 적용](https://hohodu.tistory.com/156?category=1150624)  
[JWT 인증/인가 적용기(3) - 로그인 ](https://hohodu.tistory.com/163?category=1150624)  
[JWT 인증/인가 적용기(4) - AccessToken이 만료된 경우](https://hohodu.tistory.com/165?category=1150624)
