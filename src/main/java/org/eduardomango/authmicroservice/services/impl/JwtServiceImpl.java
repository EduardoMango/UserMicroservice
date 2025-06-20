package org.eduardomango.authmicroservice.services.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.eduardomango.authmicroservice.config.JwtKeyProvider;
import org.eduardomango.authmicroservice.models.CredentialsEntity;
import org.eduardomango.authmicroservice.models.auth.AuthResponse;
import org.eduardomango.authmicroservice.models.auth.TokenRequest;
import org.eduardomango.authmicroservice.services.interfaces.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Value("${refresh-token.expiration}")
    private Long refreshTokenExpiration;

    private final JwtKeyProvider keyProvider;

    public JwtServiceImpl(JwtKeyProvider keyProvider) {
        this.keyProvider = keyProvider;
    }


    /** Extracts the username clam from a given token
     *
     * @param token of a given user
     * @return username
     */
    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    /** Generates an access token for a given user
     *
     * @param userDetails of a given user
     * @return access token for that given user
     */
    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        List<String> rolesAsString = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        claims.put("roles", rolesAsString);
        return buildToken(claims, userDetails, jwtExpiration);
    }

    /** Generates a refresh token for a given user
     *
     * @param userDetails of a given user
     * @return refresh token for that given user
     */
    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return buildToken(claims, userDetails, refreshTokenExpiration);
    }

    /**
     * Validates a JWT token by checking its signature, expiration,
     * username match, and user account status.
     *
     * @param token The JWT token to validate.
     * @param userDetails The user details to compare against the token.
     * @return True if the token is valid, false otherwise.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            // This will throw an exception if the signature is invalid
            Jwts.parserBuilder()
                    .setSigningKey(keyProvider.getPublicKey())
                    .build()
                    .parseClaimsJws(token);

            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()))
                    && !isTokenExpired(token)
                    && userDetails.isAccountNonLocked()
                    && userDetails.isEnabled();

        } catch (JwtException e) {
            return false; // Invalid token
        }
    }

    /**
     * Validates a Refresh Token by checking its signature and expiration.
     *
     * @param refreshToken The Refresh Token to validate.
     * @return True if the token is valid, false otherwise.
     */
    @Override
    public boolean validateRefreshToken(String refreshToken, UserDetails userDetails) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(keyProvider.getPublicKey())
                    .build()
                    .parseClaimsJws(refreshToken);

            final String username = extractUsername(refreshToken);
            return (username.equals(userDetails.getUsername())) &&
                    !isTokenExpired(refreshToken);

        } catch (JwtException e) {
            return false; // Invalid token
        }
    }

    /**
     * Extracts a specific claim from a given JWT token.
     * This method retrieves all claims from the token and applies a resolver function
     * to extract the desired claim.
     *
     * @param token The JWT token from which the claim is extracted.
     * @param claimsResolver A function to resolve and extract a specific claim from the token's claims.
     * @param <T> The type of the claim to be extracted.
     * @return The extracted claim of type T.
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses and extracts all claims from a given JWT token.
     *
     * @param token The JWT token to be parsed.
     * @return The claims extracted from the token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(keyProvider.getPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Generates a JWT token with the given claims, user details, and expiration time.
     *
     * @param extraClaims Additional claims to include in the token.
     * @param userDetails The user details used to set the token subject.
     * @param expiration The expiration time in milliseconds from the current time.
     * @return A signed JWT token as a String.
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(keyProvider.getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    /** Verifies if a given token is expired
     * Could be access token or refresh token
     *
     * @param token to be verified
     * @return true if token is expired, false otherwise.
     */
    private boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }
}
