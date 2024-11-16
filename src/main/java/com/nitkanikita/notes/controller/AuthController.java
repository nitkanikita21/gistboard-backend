package com.nitkanikita.notes.controller;

import com.nitkanikita.notes.component.AuthCookieUtils;
import com.nitkanikita.notes.component.JwtUtils;
import com.nitkanikita.notes.model.dto.response.UserDto;
import com.nitkanikita.notes.model.entity.User;
import com.nitkanikita.notes.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final AuthCookieUtils authCookieService;

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        response.addCookie(authCookieService.getClearAccessToken());
        response.addCookie(authCookieService.getClearRefreshToken());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> user() {
        User currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(userService.convertToDto(currentUser));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
        @CookieValue(name = "#{authCookieUtils.getNameRefreshToken()}") String refreshToken,
        HttpServletResponse response
    ) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            log.info("JWT is empty");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT token is empty");
        }

        Long userId;
        try {
            userId = jwtUtils.extractUserId(refreshToken);
        } catch (ExpiredJwtException e) {
            log.info("JWT expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Expired refresh token");
        } catch (SignatureException e) {
            log.info("JWT signature exception", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Cannot verify refresh token");
        }

        User optionalUser = userService.getById(userId);
        if (optionalUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        String newAccessToken = jwtUtils.generateAccessToken(optionalUser);
        log.info("New access token: {}", newAccessToken);

        response.addCookie(authCookieService.getAccessToken(newAccessToken));
        return ResponseEntity.ok("Token refreshed");
    }
}