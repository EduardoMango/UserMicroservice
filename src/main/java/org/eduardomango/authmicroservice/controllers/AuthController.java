package org.eduardomango.authmicroservice.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.eduardomango.authmicroservice.models.CredentialsEntity;
import org.eduardomango.authmicroservice.models.auth.*;
import org.eduardomango.authmicroservice.services.OAuth2ServiceImpl;
import org.eduardomango.authmicroservice.services.interfaces.AuthService;
import org.eduardomango.authmicroservice.services.interfaces.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
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
    @PostMapping
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest loginUserDto) {
        CredentialsEntity authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        return ResponseEntity.ok( new AuthResponse(jwtToken,authenticatedUser.getRefreshToken()));
    }

    @Operation(
            summary = "Refresh an access token",
            description = "This endpoint refreshes an access token based on a refresh token and creates a new RefreshToken."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful refresh",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshAccessToken(@RequestBody RefreshTokenRequest request) {
        AuthResponse response = authenticationService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Exchange a GitHub token for a local access token and refresh token",
            description = "This endpoint exchanges a GitHub token for a local access token and refresh token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful exchange",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid GitHub token")
    })
    @PostMapping("/exchange/github")
    public ResponseEntity<AuthResponse> exchangeGithubToken(@RequestBody TokenRequest tokenRequest) {
        AuthResponse response = oauth2Service.exchangeGithubToken(tokenRequest);

        if (response!=null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @Operation(
            summary = "Add a new user",
            description = "This endpoint adds a new user to the database."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user data")
    })
    @PostMapping("/user")
    public ResponseEntity<Void> addUser(@RequestBody CredentialsEntity user) {
        authenticationService.addUser(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
