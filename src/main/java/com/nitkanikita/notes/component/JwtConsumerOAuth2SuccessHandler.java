package com.nitkanikita.notes.component;

import com.nitkanikita.notes.model.entity.User;
import com.nitkanikita.notes.service.JwtService;
import com.nitkanikita.notes.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtConsumerOAuth2SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtService jwtService;
    private final Logger logger = LoggerFactory.getLogger(JwtConsumerOAuth2SuccessHandler.class);
    @Value("${auth.success_uri}")
    private String successUriRedirect;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException, ServletException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        User user = userService.registerOrUpdateUser(oauthUser).block();
        String token = jwtService.generateToken(user);

        setAlwaysUseDefaultTargetUrl(true);
        setDefaultTargetUrl(
            UriComponentsBuilder.fromHttpUrl(successUriRedirect)
                .queryParam("token", token)
                .build()
                .toUriString()
        );
        logger.info("Success created redirect url");

        super.onAuthenticationSuccess(request, response, authentication);
    }
}