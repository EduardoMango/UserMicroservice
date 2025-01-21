package org.eduardomango.authmicroservice.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class EnvironmentConfig {

    static {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("spring.datasource.url", Objects.requireNonNull(dotenv.get("DB_URL")));
        System.setProperty("spring.datasource.username", Objects.requireNonNull(dotenv.get("DB_USER")));
        System.setProperty("spring.datasource.password", Objects.requireNonNull(dotenv.get("DB_PASSWORD")));
        System.setProperty("jwt.secret", Objects.requireNonNull(dotenv.get("JWT_SECRET")));
        System.setProperty("jwt.expiration", Objects.requireNonNull(dotenv.get("JWT_EXPIRATION")));
    }

    @Bean
    public Dotenv dotenv() {
        return Dotenv.load();
    }
}
