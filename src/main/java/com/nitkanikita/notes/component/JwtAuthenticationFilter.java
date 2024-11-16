package com.nitkanikita.notes.component;

import com.nitkanikita.notes.model.entity.User;
import com.nitkanikita.notes.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.vavr.control.Option;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Option<String> jwt = jwtUtils.getJwtFromRequest(request);
        Long userId = null;

        log.info("Request to {} | JWT {}", request.getRequestURI(), jwt.getOrNull());

        if (!jwt.isEmpty()) {
            try {
                userId = jwtUtils.extractUserId(jwt.get());
            } catch (ExpiredJwtException e) {
                log.warn("JWT expired");
            } catch (SignatureException e) {
                log.warn("JWT signature exception");
            } catch (MalformedJwtException e) {
                log.warn(e.getMessage());
            }
        }

        // Якщо JWT є і користувач ще не аутентифікований
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Отримуємо користувача за його ID
            User user = userService.getById(userId);

            // Створюємо аутентифікацію
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Authenticated user with ID: {}", userId);
        }

        // Продовжуємо виконання фільтра
        filterChain.doFilter(request, response);
    }
}