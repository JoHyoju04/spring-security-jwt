package com.example.springsecurityjwt.service;

import com.example.springsecurityjwt.model.entity.User;
import com.example.springsecurityjwt.model.req.ReqUserSignUp;
import com.example.springsecurityjwt.model.req.ReqUserSignIn;
import com.example.springsecurityjwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public User signIn(ReqUserSignIn req) {
        BCryptPasswordEncoder encoder = bCryptPasswordEncoder;

        User user = findByEmail(req.getEmail());
        if (!encoder.matches(req.getPw(), user.getPassword())) {
            throw new IllegalArgumentException("Unexpected user");
        }
        return user;

    }

    public Long save(ReqUserSignUp req) {
        BCryptPasswordEncoder encoder = bCryptPasswordEncoder;

        return userRepository.save(User.builder()
                .email(req.getEmail())
                .password(encoder.encode(req.getPassword()))
                .build()).getId();
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }
}
