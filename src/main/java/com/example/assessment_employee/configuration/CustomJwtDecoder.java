package com.example.assessment_employee.configuration;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;

@Component
@Slf4j
public class CustomJwtDecoder implements JwtDecoder {

    @Value("${jwt.secret:mySecretKey}")
    private String jwtSecret;

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            // Verify JWT signature
            JWSVerifier verifier = new MACVerifier(jwtSecret.getBytes());
            if (!signedJWT.verify(verifier)) {
                throw new JwtException("JWT signature verification failed");
            }

            // Check expiration
            Instant expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime().toInstant();
            if (Instant.now().isAfter(expirationTime)) {
                throw new JwtException("JWT token has expired");
            }

            return new Jwt(token,
                    signedJWT.getJWTClaimsSet().getIssueTime().toInstant(),
                    expirationTime,
                    signedJWT.getHeader().toJSONObject(),
                    signedJWT.getJWTClaimsSet().getClaims());

        } catch (ParseException e) {
            log.error("Failed to parse JWT token: {}", e.getMessage());
            throw new JwtException("Invalid JWT token format");
        } catch (JOSEException e) {
            log.error("JWT verification failed: {}", e.getMessage());
            throw new JwtException("JWT verification failed");
        } catch (Exception e) {
            log.error("Unexpected error during JWT decoding: {}", e.getMessage());
            throw new JwtException("JWT decoding failed");
        }
    }
}
