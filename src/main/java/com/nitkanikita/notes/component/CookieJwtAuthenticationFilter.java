package com.nitkanikita.notes.component;

import com.nitkanikita.notes.service.AuthCookieService;
import com.nitkanikita.notes.service.JwtService;
import com.nitkanikita.notes.service.UserService;
import io.vavr.collection.List;
import io.vavr.control.Option;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CookieJwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;
    private final AuthCookieService authCookieService;
    private final Logger LOGGER = LoggerFactory.getLogger(CookieJwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        LOGGER.info("Request to {}", request.getRequestURI());

        Option<String> jwt = getJwtFromRequest(request);

        LOGGER.info(
            "JWT: {}",
            jwt.map(s -> s.substring(0, 100)).getOrNull()
        );

        if (jwt.isEmpty()) {
//            filterChain.doFilter(request, response);
            LOGGER.info("JWT is empty");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("JWT is missing or invalid.");
            return;
        }

        if (jwtService.isTokenExpired(jwt.get())) {
//            filterChain.doFilter(request, response);
            LOGGER.info("JWT is expired");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("JWT is expired.");
            SecurityContextHolder.getContext().setAuthentication(null);
            return;
        }
        String username = jwtService.extractUserName(jwt.get());

        LOGGER.info("Username: {}", username);

        userService.getByUsername(username)
            .doOnSuccess(user -> {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                LOGGER.info("Request authenticated");
            })
            .doOnTerminate(() -> {
                try {
                    filterChain.doFilter(request, response);
                } catch (IOException | ServletException e) {
                    LOGGER.error("Error", e);
                }
            })
            .subscribe();

    }

    private Option<String> getJwtFromRequest(HttpServletRequest request) {
        List<Cookie> cookies = List.of(request.getCookies());
        Option<Cookie> accessToken = cookies.find(c ->
            c.getName().equals(authCookieService.getNameAccessToken())
        );
        return accessToken.map(Cookie::getValue);
    }
}