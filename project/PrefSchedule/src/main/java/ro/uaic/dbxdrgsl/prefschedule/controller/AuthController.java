package ro.uaic.dbxdrgsl.prefschedule.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ro.uaic.dbxdrgsl.prefschedule.dto.AuthResponse;
import ro.uaic.dbxdrgsl.prefschedule.dto.LoginRequest;
import ro.uaic.dbxdrgsl.prefschedule.dto.RegisterRequest;
import ro.uaic.dbxdrgsl.prefschedule.model.AppUser;
import ro.uaic.dbxdrgsl.prefschedule.repository.AppUserRepository;
import ro.uaic.dbxdrgsl.prefschedule.security.JwtTokenProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication controller with JWT-based login and registration endpoints.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "JWT-based authentication endpoints")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * JWT-based login endpoint that authenticates users and issues tokens.
     */
    @Operation(summary = "JWT login", 
               description = "Authenticates user credentials and returns a JWT token. " +
                           "Use the returned token in the Authorization header as 'Bearer {token}'.")
    @ApiResponse(responseCode = "200", description = "Successfully authenticated")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtTokenProvider.generateToken(userDetails);

        // Extract role from authorities
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .orElse("USER");

        AuthResponse response = new AuthResponse(jwt, userDetails.getUsername(), role);
        return ResponseEntity.ok(response);
    }

    /**
     * User registration endpoint with BCrypt password encryption.
     */
    @Operation(summary = "Register new user", 
               description = "Creates a new user account with encrypted password. " +
                           "Role can be STUDENT, INSTRUCTOR, or ADMIN.")
    @ApiResponse(responseCode = "201", description = "User successfully registered")
    @ApiResponse(responseCode = "400", description = "Invalid input or username/email already exists")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        // Check if username already exists
        if (appUserRepository.existsByUsername(request.getUsername())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Username already exists");
            return ResponseEntity.badRequest().body(error);
        }

        // Check if email already exists
        if (appUserRepository.existsByEmail(request.getEmail())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Email already exists");
            return ResponseEntity.badRequest().body(error);
        }

        // Validate role
        AppUser.Role role;
        try {
            role = AppUser.Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid role. Must be STUDENT, INSTRUCTOR, or ADMIN");
            return ResponseEntity.badRequest().body(error);
        }

        // Create new user with encrypted password
        AppUser user = AppUser.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .email(request.getEmail())
                .role(role)
                .enabled(true)
                .build();

        appUserRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("username", user.getUsername());
        response.put("role", user.getRole().name());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get the current authenticated user information.
     */
    @Operation(summary = "Get current user", description = "Returns information about the currently authenticated user")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user info")
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("username", auth.getName());
            userInfo.put("authorities", auth.getAuthorities());
            userInfo.put("authenticated", true);
            return ResponseEntity.ok(userInfo);
        }
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", "anonymous");
        userInfo.put("authorities", "[]");
        userInfo.put("authenticated", false);
        return ResponseEntity.ok(userInfo);
    }
}
