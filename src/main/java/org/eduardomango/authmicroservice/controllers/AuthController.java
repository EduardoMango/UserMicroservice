package org.eduardomango.authmicroservice.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.eduardomango.authmicroservice.models.CredentialsEntity;
import org.eduardomango.authmicroservice.models.auth.*;
import org.eduardomango.authmicroservice.services.JpaUserDetailsService;
import org.eduardomango.authmicroservice.services.interfaces.AuthService;
import org.eduardomango.authmicroservice.services.interfaces.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Operations related to user authentication and account management")
public class AuthController {

    private final JwtService jwtService;
    private final AuthService authenticationService;
    private final RestTemplate restTemplate;
    private final JpaUserDetailsService userService;

    @Autowired
    public AuthController(JwtService jwtService, AuthService authenticationService, RestTemplate restTemplate, JpaUserDetailsService userService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.restTemplate = restTemplate;
        this.userService = userService;
    }

    @Operation(
            summary = "Authenticate a user and return a JWT token",
            description = "This endpoint authenticates a user with their credentials and returns a JWT token if the authentication is successful."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful authentication",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public AuthResponse authenticate(@RequestBody AuthRequest loginUserDto) {
        CredentialsEntity authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        return new AuthResponse(jwtToken,authenticatedUser.getRefreshToken());
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshAccessToken(@RequestBody RefreshTokenRequest request) {
        AuthResponse response = authenticationService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/exchange/github")
    public ResponseEntity<?> exchangeGithubToken(@RequestBody TokenRequest tokenRequest) {
        String githubToken = tokenRequest.getToken();

        // 1. Validar token con GitHub
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

            // 2. Verificar si el usuario existe o crear uno nuevo
            CredentialsEntity localUser = userService.findOrCreateUser(githubUser);

            // 3. Generar el token JWT propio
            String jwt = jwtService.generateToken(localUser);

      // 4. Devolver el token JWT
      return ResponseEntity.ok(new AuthResponse(jwt,localUser.getRefreshToken()));

        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid GitHub token");
        }
    }

    @GetMapping("/success")
    public ResponseEntity<String> loginSuccess(OAuth2AuthenticationToken authentication) {
        return ResponseEntity.ok("Login successful with user: " + authentication.getPrincipal().getName());
    }

    @GetMapping("/failure")
    public ResponseEntity<String> loginFailure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication error");
    }

    @GetMapping("/test")
    public ResponseEntity<String> testTokenValidity(){
        return ResponseEntity.ok("Token is valid");
    }

}
