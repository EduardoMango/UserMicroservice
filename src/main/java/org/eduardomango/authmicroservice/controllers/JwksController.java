package org.eduardomango.authmicroservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.eduardomango.authmicroservice.config.JwtKeyProvider;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.JWKSet;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

@RestController
@RequestMapping("/.well-known")
public class JwksController {
  private final JwtKeyProvider keyProvider;

  public JwksController(JwtKeyProvider keyProvider) {
    this.keyProvider = keyProvider;
  }

  @Operation(
          summary = "Get JSON Web Key Set (JWKS)",
          description = "This endpoint returns a JSON Web Key Set (JWKS) containing the public key used to sign JWT tokens."
  )
  @Cacheable("jwksCache")
  @GetMapping("/jwks.json")
  public Map<String, Object> getJwks() {
    RSAPublicKey publicKey = (RSAPublicKey) keyProvider.getPublicKey();
    RSAKey jwk = new RSAKey.Builder(publicKey).keyID("rsa-key").build();
      return new JWKSet(jwk).toJSONObject();
  }
}
