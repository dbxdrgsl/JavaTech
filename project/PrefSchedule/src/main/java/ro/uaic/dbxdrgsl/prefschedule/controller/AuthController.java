package ro.uaic.dbxdrgsl.prefschedule.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Mock authentication controller for testing Spring Security integration.
 * This is a simplified login endpoint for demonstration purposes.
 */
@RestController
@Tag(name = "Authentication", description = "Mock authentication endpoints")
public class AuthController {

    /**
     * Mock login endpoint that accepts username and password.
     * NOTE: This is a mock endpoint for demonstration purposes only.
     * Actual authentication is handled by Spring Security's HTTP Basic Auth.
     * In Section 6 Homework, this will be replaced with JWT token generation.
     */
    @Operation(summary = "Mock login endpoint (demonstration only)", 
               description = "Accepts username and password. Returns a mock authentication response. " +
                           "NOTE: This does not perform actual authentication. " +
                           "Use HTTP Basic Auth (Authorization header) to authenticate API requests. " +
                           "This endpoint will be enhanced with JWT token generation in Section 6 Homework.")
    @ApiResponse(responseCode = "200", description = "Mock login response returned")
    @PostMapping("/api/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        // This is a mock endpoint - actual authentication is handled by Spring Security
        // In Section 6 Homework, this will validate credentials and issue JWT tokens
        
        LoginResponse response = new LoginResponse(
                "Mock login response for: " + request.getUsername() + 
                ". Note: Use HTTP Basic Auth for actual authentication.",
                LocalDateTime.now(),
                "mock-token-" + System.currentTimeMillis()
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get the current authenticated user information.
     */
    @Operation(summary = "Get current user", description = "Returns information about the currently authenticated user")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user info")
    @GetMapping("/api/me")
    public ResponseEntity<UserInfo> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            UserInfo userInfo = new UserInfo(
                    auth.getName(),
                    auth.getAuthorities().toString(),
                    true
            );
            return ResponseEntity.ok(userInfo);
        }
        
        return ResponseEntity.ok(new UserInfo("anonymous", "[]", false));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse {
        private String message;
        private LocalDateTime timestamp;
        private String token;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String username;
        private String authorities;
        private boolean authenticated;
    }
}
