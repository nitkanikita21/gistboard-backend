package com.nitkanikita.notes.component;

import com.nitkanikita.notes.model.entity.User;
import com.nitkanikita.notes.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
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
import reactor.core.scheduler.Schedulers;

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


        if(!jwt.isEmpty()) {
            try {
                userId = jwtUtils.extractUserId(jwt.get());
            } catch (ExpiredJwtException e) {
                log.info("JWT expired");
            } catch (SignatureException e) {
                log.info("JWT signature exception");
            }
        }

        final Long finalUserId = userId;
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            userService.getById(userId)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(user -> {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("Authenticated user with ID: {}", finalUserId);
                })
                .doOnTerminate(() -> {
                    try {
                        filterChain.doFilter(request, response);
                    } catch (IOException | ServletException e) {
                        log.error("Error in filter chain", e);
                    }
                })
                .subscribe();
        } else {
            filterChain.doFilter(request, response);
        }
    }


}