package org.eduardomango.authmicroservice.config;

import lombok.Getter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Collectors;

@Getter
@Component
public class JwtKeyProvider {
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public JwtKeyProvider() throws Exception {
        this.privateKey = loadPrivateKey();
        this.publicKey = loadPublicKey();
    }

    private PrivateKey loadPrivateKey() throws Exception {
        String keyPEM = readKeyFile("private_key.pem");
        byte[] keyBytes = Base64.getDecoder().decode(keyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    private PublicKey loadPublicKey() throws Exception {
        String keyPEM = readKeyFile("public_key.pem");
        byte[] keyBytes = Base64.getDecoder().decode(keyPEM);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    private String readKeyFile(String filename) throws IOException {
        return Files.readAllLines(new ClassPathResource(filename).getFile().toPath())
                .stream()
                .filter(line -> !line.startsWith("-----")) // Filtra encabezados y pies de página
                .collect(Collectors.joining());
    }
}

