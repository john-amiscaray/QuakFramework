package io.john.amiscaray.quak.security.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import io.john.amiscaray.quak.core.di.provider.annotation.Instantiate;
import io.john.amiscaray.quak.core.di.provider.annotation.ManagedType;
import io.john.amiscaray.quak.security.auth.principal.Principal;
import io.john.amiscaray.quak.security.config.SecurityConfig;

import java.util.Date;

/**
 * Used to manage and produce JWTs
 */
@ManagedType
public class JwtUtil {

    private final String secretKey;
    private final long expirationTime; // 10 hours

    @Instantiate
    public JwtUtil(SecurityConfig securityConfig) {
        secretKey = securityConfig.jwtSecretKey();
        expirationTime = securityConfig.jwtSecretExpiryTime();
    }

    /**
     * Generates a token from the user's {@link io.john.amiscaray.quak.security.auth.principal.Principal}.
     * @param principal The user's principal.
     * @return The JWT token as a String.
     */
    public String generateToken(Principal principal) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        return JWT.create()
                .withSubject(principal.getSecurityID())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .sign(algorithm);
    }

    /**
     * Validates and decodes a given JWT.
     * @param token The JWT as a String.
     * @return The decoded JWT.
     */
    public DecodedJWT validateTokenAndGetDecoded(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm)
                .build();
        return verifier.verify(token);
    }

    /**
     * Extracts the subject of a JWT.
     * @param token The JWT.
     * @return The subject (the security ID of the user).
     */
    public String extractSubject(String token) {
        return validateTokenAndGetDecoded(token).getSubject();
    }

    /**
     * Checks if a JWT is expired.
     * @param token The JWT.
     * @return Whether the token is expired.
     */
    public boolean isTokenExpired(String token) {
        Date expiration = validateTokenAndGetDecoded(token).getExpiresAt();
        return expiration.before(new Date());
    }
}
