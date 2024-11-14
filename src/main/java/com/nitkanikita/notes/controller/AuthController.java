package com.nitkanikita.notes.controller;

import com.nitkanikita.notes.model.entity.User;
import com.nitkanikita.notes.repository.UserRepository;
import com.nitkanikita.notes.service.AuthCookieService;
import com.nitkanikita.notes.service.JwtService;
import com.nitkanikita.notes.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final UserService userService;
    private final AuthCookieService authCookieService;

    @PostMapping("/logout")
    public Mono<ResponseEntity<?>> logout(HttpServletResponse response) {
        response.addCookie(authCookieService.getClearAccessToken());
        response.addCookie(authCookieService.getClearRefreshToken());

        return Mono.just(ResponseEntity.status(HttpStatus.OK).build());
    }
    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> user(
        HttpServletResponse response,
        Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(jwtService.getClaims(user));
    }

    @PostMapping("/refresh")
    public Mono<ResponseEntity<?>> refreshToken(
        @CookieValue(name = "#{authCookieService.getNameRefreshToken()}") String refreshToken,
        HttpServletResponse response
    ) {
        // Перевірка refresh токену
        if (refreshToken == null || jwtService.isTokenExpired(refreshToken)) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token"));
        }

        // Отримання інформації про користувача, пов'язану з refresh токеном
        String username = jwtService.extractUserName(refreshToken);

        return userService.getByUsername(username)
            .flatMap(optionalUser -> {
                if(optionalUser == null) {
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found"));
                }
                String newAccessToken = jwtService.generateToken(optionalUser);

                response.addCookie(authCookieService.getAccessToken(newAccessToken));

                return Mono.just(ResponseEntity.ok("Token refreshed"));
            });
    }
}