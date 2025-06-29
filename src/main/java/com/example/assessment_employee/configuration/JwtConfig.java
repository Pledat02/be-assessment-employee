package com.example.assessment_employee.configuration;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.spec.SecretKeySpec;

@Configuration
public class JwtConfig {
    
    @Value("${jwt.secret:mySecretKey}")
    private String jwtSecret;
    
    @Bean
    public JwtEncoder jwtEncoder() {
        // Create secret key for HMAC signing
        SecretKeySpec secretKey = new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256");
        
        // Create immutable secret source
        ImmutableSecret secret = new ImmutableSecret<>(secretKey);
        
        // Return Nimbus JWT encoder
        return new NimbusJwtEncoder(secret);
    }
}
