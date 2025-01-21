package org.eduardomango.authmicroservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.eduardomango.authmicroservice.models.CredentialsEntity;
import org.eduardomango.authmicroservice.models.auth.AuthRequest;
import org.eduardomango.authmicroservice.models.auth.AuthResponse;
import org.eduardomango.authmicroservice.services.interfaces.AuthService;
import org.eduardomango.authmicroservice.services.interfaces.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Operations related to user authentication and account management")
public class AuthController {

    private final JwtService jwtService;

    private final AuthService authenticationService;

    @Autowired
    public AuthController(JwtService jwtService, AuthService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
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

        return new AuthResponse(jwtToken);
    }

    @GetMapping("/test")
    public ResponseEntity<String> testTokenValidity(){
        return ResponseEntity.ok("Token is valid");
    }

}
