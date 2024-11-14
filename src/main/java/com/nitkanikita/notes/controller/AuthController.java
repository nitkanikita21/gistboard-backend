package com.nitkanikita.notes.controller;

import com.nitkanikita.notes.model.dto.UserInfoDto;
import com.nitkanikita.notes.model.entity.User;
import com.nitkanikita.notes.component.AuthCookieUtils;
import com.nitkanikita.notes.component.JwtUtils;
import com.nitkanikita.notes.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.vavr.control.Option;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final AuthCookieUtils authCookieService;

    @PostMapping("/logout")
    public Mono<ResponseEntity<?>> logout(HttpServletResponse response) {
        response.addCookie(authCookieService.getClearAccessToken());
        response.addCookie(authCookieService.getClearRefreshToken());

        return Mono.just(ResponseEntity.status(HttpStatus.OK).build());
    }
    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public Mono<ResponseEntity<UserInfoDto>> user() {
        return userService.getCurrentUser().flatMap(user ->
            Mono.just(ResponseEntity.ok(new UserInfoDto(user)))
        );
    }

    @PostMapping("/refresh")
    public Mono<ResponseEntity<?>> refreshToken(
        @CookieValue(name = "#{authCookieUtils.getNameRefreshToken()}") String refreshToken,
        HttpServletRequest request,
        HttpServletResponse response
    ) {

        Option<String> jwt = Option.of(refreshToken);

        if(jwt.isEmpty()) {
            log.info("JWT is empty");
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT token is empty"));
        }

        Long userId;

        try {
            userId = jwtUtils.extractUserId(jwt.get());
        } catch (ExpiredJwtException e) {
            log.info("JWT expired");
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Expired refresh token"));
        } catch (SignatureException e) {
            log.info("JWT signature exception", e);
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Cannot verify refresh token"));
        }

        return userService.getById(userId)
            .flatMap(optionalUser -> {
                if(optionalUser == null) {
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found"));
                }
                String newAccessToken = jwtUtils.generateAccessToken(optionalUser);

                log.info("New access token: {}", newAccessToken);

                response.addCookie(authCookieService.getAccessToken(newAccessToken));
                return Mono.just(ResponseEntity.ok("Token refreshed"));
            });
    }
}