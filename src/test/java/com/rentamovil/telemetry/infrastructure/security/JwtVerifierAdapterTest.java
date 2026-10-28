package com.rentamovil.telemetry.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.rentamovil.telemetry.domain.entity.AuthenticatedUser;
import com.rentamovil.telemetry.domain.exception.InvalidTokenException;

/** Only the RS256 public key iam publishes at GET /jwks is held here: never the private key. */
class JwtVerifierAdapterTest {

    private static RSAKey iamKey;
    private static RSAKey otherKey;
    private static JwtVerifierAdapter verifier;

    @BeforeAll
    static void setUp() throws Exception {
        iamKey = new RSAKeyGenerator(2048).keyID("iam").generate();
        otherKey = new RSAKeyGenerator(2048).keyID("iam").generate();
        verifier = new JwtVerifierAdapter(iamKey.toPublicJWK());
    }

    private static JWTClaimsSet.Builder validClaims() {
        return new JWTClaimsSet.Builder()
                .subject("12")
                .claim("personId", 34)
                .claim("role", "ADMIN")
                .claim("permissions", List.of("tracking:read"))
                .expirationTime(Date.from(Instant.now().plusSeconds(600)));
    }

    private static String sign(RSAKey key, JWTClaimsSet claims) throws Exception {
        SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(key.getKeyID()).build(), claims);
        jwt.sign(new RSASSASigner(key));
        return jwt.serialize();
    }

    @Test
    void acceptsTokenSignedByIam() throws Exception {
        AuthenticatedUser user = verifier.verify(sign(iamKey, validClaims().build()));

        assertThat(user.getUserId()).isEqualTo(12);
        assertThat(user.getPersonId()).isEqualTo(34);
        assertThat(user.getRole()).isEqualTo("ADMIN");
        assertThat(user.hasPermission("tracking:read")).isTrue();
    }

    @Test
    void rejectsExpiredToken() throws Exception {
        String expired = sign(iamKey, validClaims().expirationTime(Date.from(Instant.now().minusSeconds(600))).build());

        assertThatThrownBy(() -> verifier.verify(expired)).isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void rejectsTokenSignedWithAnotherKey() throws Exception {
        String forged = sign(otherKey, validClaims().build());

        assertThatThrownBy(() -> verifier.verify(forged)).isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void rejectsHs256() throws Exception {
        SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), validClaims().build());
        jwt.sign(new MACSigner("a-shared-secret-of-at-least-32-bytes!!!!"));

        assertThatThrownBy(() -> verifier.verify(jwt.serialize())).isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void rejectsTokenWithoutPersonId() throws Exception {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("12")
                .claim("role", "ADMIN")
                .expirationTime(Date.from(Instant.now().plusSeconds(600)))
                .build();

        assertThatThrownBy(() -> verifier.verify(sign(iamKey, claims))).isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void rejectsGarbage() {
        assertThatThrownBy(() -> verifier.verify("not-a-jwt")).isInstanceOf(InvalidTokenException.class);
    }
}
