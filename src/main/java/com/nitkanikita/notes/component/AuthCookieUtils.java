package com.nitkanikita.notes.component;

import jakarta.servlet.http.Cookie;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class AuthCookieUtils {
    @Value("${auth.cookie.domain}")
    private String domain;
    @Value("${auth.cookie.path}")
    private String path;
    @Value("${auth.cookie.name.access_token}")
    private String nameAccessToken;
    @Value("${auth.cookie.name.refresh_token}")
    private String nameRefreshToken;

    public Cookie getAccessToken(String token) {
        Cookie cookie = new Cookie(nameAccessToken, token);
        cookie.setDomain(domain);
        cookie.setPath(path);
        return cookie;
    }
    public Cookie getRefreshToken(String token) {
        Cookie cookie = new Cookie(nameRefreshToken, token);
        cookie.setHttpOnly(true);
        cookie.setDomain(domain);
        cookie.setPath(path);
        return cookie;
    }

    public Cookie getClearAccessToken() {
        return clearCookie(getAccessToken(null));
    }
    public Cookie getClearRefreshToken() {
        return clearCookie(getRefreshToken(null));
    }

    private Cookie clearCookie(Cookie cookie) {
        cookie.setMaxAge(0);
        return cookie;
    }
}
