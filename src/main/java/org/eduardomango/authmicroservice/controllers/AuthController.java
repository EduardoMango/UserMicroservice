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
import org.eduardomango.authmicroservice.services.OAuth2ServiceImpl;
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
    private final OAuth2ServiceImpl oauth2Service;

    @Autowired
    public AuthController(JwtService jwtService, AuthService authenticationService, OAuth2ServiceImpl oauth2Service) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.oauth2Service = oauth2Service;
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
    public ResponseEntity<AuthResponse> exchangeGithubToken(@RequestBody TokenRequest tokenRequest) {
        AuthResponse response = oauth2Service.exchangeGithubToken(tokenRequest);

        if (response!=null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
