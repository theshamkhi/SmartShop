package com.smartshop.smartshop.service;

import com.smartshop.smartshop.model.dto.request.LoginRequest;
import com.smartshop.smartshop.model.dto.response.LoginResponse;
import com.smartshop.smartshop.exception.UnauthorizedException;
import com.smartshop.smartshop.model.entity.User;
import com.smartshop.smartshop.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    public static final String SESSION_USER_KEY = "authenticatedUser";

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request, HttpSession session) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        session.setAttribute(SESSION_USER_KEY, user);

        return LoginResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .message("Login successful")
                .build();
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    public User getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute(SESSION_USER_KEY);
        if (user == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        return user;
    }
}