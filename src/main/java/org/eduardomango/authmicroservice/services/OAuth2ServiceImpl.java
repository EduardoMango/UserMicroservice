package org.eduardomango.authmicroservice.services;

import org.eduardomango.authmicroservice.models.CredentialsEntity;
import org.eduardomango.authmicroservice.models.auth.AuthResponse;
import org.eduardomango.authmicroservice.models.auth.GithubUserResponse;
import org.eduardomango.authmicroservice.models.auth.TokenRequest;
import org.eduardomango.authmicroservice.services.interfaces.AuthService;
import org.eduardomango.authmicroservice.services.interfaces.JwtService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OAuth2ServiceImpl {

    private final JpaUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final RestTemplate restTemplate;

    public OAuth2ServiceImpl(JpaUserDetailsService userDetailsService, JwtService jwtService, RestTemplate restTemplate) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.restTemplate = restTemplate;
    }

    public AuthResponse exchangeGithubToken(TokenRequest tokenRequest) {
        String githubToken = tokenRequest.getToken();

        // 1. Validate github token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<GithubUserResponse> response = restTemplate.exchange(
                    "https://api.github.com/user",
                    HttpMethod.GET,
                    entity,
                    GithubUserResponse.class
            );

            GithubUserResponse githubUser = response.getBody();
      System.out.println(response.getBody().getEmail());

            // 2. Verify if user exists in our database. If not, create it
            CredentialsEntity localUser = userDetailsService.findOrCreateUser(githubUser);

            // 3. Generate token
            String jwt = jwtService.generateToken(localUser);

            return new AuthResponse(jwt, localUser.getRefreshToken());
    } catch (Exception e) {
        return null;
    }
    }
}
