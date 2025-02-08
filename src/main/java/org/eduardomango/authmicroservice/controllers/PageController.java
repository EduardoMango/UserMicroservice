package org.eduardomango.authmicroservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PageController {

    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    public PageController(OAuth2AuthorizedClientService oAuth2AuthorizedClientService) {
        this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
    }

    @Operation(
            summary = "Successfully logged in",
            description = "Login success page"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful login"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/success")
    public String loginSuccess(Model model, Authentication authentication) {

        if (authentication instanceof OAuth2AuthenticationToken token) {
            OAuth2AuthorizedClient client = oAuth2AuthorizedClientService.loadAuthorizedClient(
                    token.getAuthorizedClientRegistrationId(), token.getName());

            String accessToken = client.getAccessToken().getTokenValue();

            System.out.println(accessToken);

            OAuth2User user = token.getPrincipal();
            model.addAttribute("username", user.getAttribute("login"));
            model.addAttribute("name", user.getAttribute("name"));
            model.addAttribute("email", user.getAttribute("email"));
            model.addAttribute("profileUrl", "https://github.com/" + user.getAttribute("login"));
            model.addAttribute( "avatarUrl", user.getAttribute("avatar_url"));
            model.addAttribute("githubToken", accessToken);
        } else {
            model.addAttribute("username", (String) SecurityContextHolder.getContext().getAuthentication().getName());
        }
        return "auth";
    }

    @Operation(
            summary = "Failed login",
            description = "Login failure page"
    )
    @GetMapping("/failure")
    public ResponseEntity<String> loginFailure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication error");
    }

    @Operation(
            summary = "Login page",
            description = "Login page with login formulary and GitHub auth button"
    )
    @GetMapping("/login")
    public String home(Model model, @AuthenticationPrincipal OAuth2User user) {
        if (user != null) {
            model.addAttribute("name", user.getAttribute("name"));
        }

        return "login";
    }

    @Operation(
            summary = "Logout",
            description = "Logout page"
    )
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getSession().invalidate();
        response.sendRedirect("/");
        return "redirect:/";
    }

    @Operation(
            summary = "Validate token",
            description = "Validates token and returns success message"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token is valid"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/validate-token")
    public ResponseEntity<Map<String, String>> testTokenValidity(){
        Map<String, String> response = new HashMap<>();
        response.put("message", "Token is valid");
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Register page",
            description = "Register page with register formulary"
    )
    @GetMapping("/register")
    public String register() {
        return "register";
    }
}
