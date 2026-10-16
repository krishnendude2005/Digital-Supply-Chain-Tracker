package com.SupplyChain.DigitalSupplyChainTracker.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
@Component
public class JwtUtils {
    private static final String SECRET_KEY = "mysecretkeyandthisisheretomakeitlengthyveryverylengthy";
    private static final int EXPIRATION_TIME = 60 * 60 * 1000;
    private final Instant now = Instant.now();

    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    public String generateToken(Authentication authentication) {

        String username = authentication.getName();

        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(EXPIRATION_TIME)))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean isValidToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);

        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();

    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token) {
        return getExpirationDate(token).before(new Date());
    }
    private Date getExpirationDate(String token) {
        return extractAllClaims(token).getExpiration();
    }


}
