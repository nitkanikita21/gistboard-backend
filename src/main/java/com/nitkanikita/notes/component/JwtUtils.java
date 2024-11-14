package com.nitkanikita.notes.component;

import com.nitkanikita.notes.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.vavr.control.Option;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtUtils {
    @Value("${jwt.secret_key}")
    private String jwtSigningKey;

    @Value("${jwt.access_lifetime}")
    private Duration jwtAccessLifetime;

    @Value("${jwt.refresh_lifetime}")
    private Duration jwtRefreshLifetime;

    public Long extractUserId(String token) {
        return extractAllClaims(token).get("id", Long.class);
    }

    public Map<String, Object> getClaims(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User user) {
            claims.put("id", user.getId());
            claims.put("username", user.getUsername());
            claims.put("role", user.getRole());
        }
        return claims;
    }

    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails, jwtAccessLifetime);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, jwtRefreshLifetime);
    }


    private String generateToken(UserDetails userDetails, Duration duration) {
        return Jwts.builder()
            .claims(getClaims(userDetails))
            .subject(userDetails.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + duration.toMillis()))
            .signWith(getSigningKey(), Jwts.SIG.HS512).compact();
    }


    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();

    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Option<String> getJwtFromRequest(HttpServletRequest request) {
        return Option.of(request.getHeader(HttpHeaders.AUTHORIZATION)).map(s -> s.substring(7));
    }
}