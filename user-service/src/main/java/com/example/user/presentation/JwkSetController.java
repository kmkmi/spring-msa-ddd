package com.example.user.presentation;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import java.security.interfaces.RSAPublicKey;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class JwkSetController {

    private static final Logger logger = LoggerFactory.getLogger(JwkSetController.class);

    @GetMapping(value = "/oauth2/jwks", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getJwks() {
        logger.info("JWKS endpoint called");
        try {
            // RSA 공개키 로드
            String key = new String(Files.readAllBytes(Paths.get("/app/keys/public_key.pem")))
                    .replaceAll("-----BEGIN (.*)-----", "")
                    .replaceAll("-----END (.*)-----", "")
                    .replaceAll("\r", "")
                    .replaceAll("\n", "");
            byte[] keyBytes = Base64.getDecoder().decode(key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            RSAPublicKey publicKey = (RSAPublicKey) kf.generatePublic(spec);
            RSAKey jwk = new RSAKey.Builder(publicKey)
                    .keyID("user-service-rs256")
                    .algorithm(com.nimbusds.jose.JWSAlgorithm.RS256)
                    .build();
            JWKSet jwkSet = new JWKSet(jwk);
            return ResponseEntity.ok(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(jwkSet.toJSONObject()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error generating JWKS: " + e.getMessage());
        }
    }
} 