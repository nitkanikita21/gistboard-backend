package com.nitkanikita.notes.service;

import com.nitkanikita.notes.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtService {
    @Value("${jwt.secret_key}")
    private String jwtSigningKey;

    @Value("${jwt.expiration}")
    private Long jwtExpirationMs;

    @Value("${jwt.refresh_expiration}")
    private Long jwtRefreshExpirationMs;

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Map<String, Object> getClaims(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User customUserDetails) {
            claims.put("id", customUserDetails.getId());
            claims.put("username", customUserDetails.getUsername());
            claims.put("email", customUserDetails.getEmail());
            claims.put("role", customUserDetails.getRole());
            claims.put("avatarUrl", customUserDetails.getAvatarUrl());
        }
        return claims;
    }


    public String generateToken(UserDetails userDetails) {
        return generateToken(getClaims(userDetails), userDetails, jwtExpirationMs);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(getClaims(userDetails), userDetails, jwtRefreshExpirationMs);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }


    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, Long expiration) {
        return Jwts.builder()
            .claims(extraClaims)
            .subject(userDetails.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey(), Jwts.SIG.HS512).compact();
    }

    public boolean isTokenExpired(String token) {
        try {
            extractAllClaims(token);  // This will throw an exception if the token is expired
            return false; // If no exception is thrown, the token is not expired
        } catch (ExpiredJwtException e) {
            return true;  // If an ExpiredJwtException is thrown, the token is expired
        }
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
//            .decryptWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();

    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}