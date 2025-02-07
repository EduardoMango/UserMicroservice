package org.eduardomango.authmicroservice.controllers;

import org.eduardomango.authmicroservice.config.JwtKeyProvider;
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

  @GetMapping("/jwks.json")
  public Map<String, Object> getJwks() {
    RSAPublicKey publicKey = (RSAPublicKey) keyProvider.getPublicKey();
    RSAKey jwk = new RSAKey.Builder(publicKey).keyID("rsa-key").build();
      return new JWKSet(jwk).toJSONObject();
  }
}
