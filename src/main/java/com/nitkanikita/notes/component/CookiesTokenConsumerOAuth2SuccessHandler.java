package com.nitkanikita.notes.component;

import com.nitkanikita.notes.model.entity.User;
import com.nitkanikita.notes.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CookiesTokenConsumerOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtUtils jwtService;
    private final AuthCookieUtils authCookieService;
    private final Logger logger = LoggerFactory.getLogger(CookiesTokenConsumerOAuth2SuccessHandler.class);

    @Value("${auth.success_uri}")
    private String successUriRedirect;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        User user = userService.registerOrUpdateUser(oauthUser);
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        Cookie refreshTokenCookie = authCookieService.getRefreshToken(refreshToken);
        Cookie accessTokenCookie = authCookieService.getAccessToken(accessToken);



        logger.info("Success authorized {} | refresh: {}", accessToken, refreshToken);

//        SecurityContextHolder.getContext().setAuthentication(null);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
        response.sendRedirect(successUriRedirect);

    }
}