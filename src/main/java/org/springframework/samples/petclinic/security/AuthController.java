package org.springframework.samples.petclinic.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "Authentication management API")
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@PostMapping("/login")
	@Operation(summary = "User login", description = "Authenticate user and return JWT token")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Login successful",
					content = @Content(schema = @Schema(implementation = Map.class))),
			@ApiResponse(responseCode = "400", description = "Invalid credentials",
					content = @Content(schema = @Schema(implementation = Map.class))) })
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

			UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
			String token = jwtUtil.generateToken(userDetails.getUsername());

			Map<String, Object> response = new HashMap<>();
			response.put("token", token);
			response.put("type", "Bearer");
			response.put("username", userDetails.getUsername());
			response.put("expiresIn", 86400); // 24 hours in seconds

			return ResponseEntity.ok(response);
		}
		catch (Exception e) {
			Map<String, String> error = new HashMap<>();
			error.put("error", "Invalid username or password");
			return ResponseEntity.badRequest().body(error);
		}
	}

	@PostMapping("/validate")
	@Operation(summary = "Validate token", description = "Validate JWT token and return user information")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Token validation result",
			content = @Content(schema = @Schema(implementation = Map.class))) })
	public ResponseEntity<?> validateToken(
			@RequestHeader("Authorization") @Parameter(description = "Bearer token") String authHeader) {
		try {
			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				String token = authHeader.substring(7);
				String username = jwtUtil.extractUsername(token);

				if (jwtUtil.validateToken(token, username)) {
					Map<String, Object> response = new HashMap<>();
					response.put("valid", true);
					response.put("username", username);
					return ResponseEntity.ok(response);
				}
			}

			Map<String, Object> response = new HashMap<>();
			response.put("valid", false);
			return ResponseEntity.ok(response);
		}
		catch (Exception e) {
			Map<String, Object> response = new HashMap<>();
			response.put("valid", false);
			response.put("error", "Invalid token");
			return ResponseEntity.ok(response);
		}
	}

	@Schema(description = "Login request")
	public static class LoginRequest {

		@Schema(description = "Username", example = "admin")
		private String username;

		@Schema(description = "Password", example = "password")
		private String password;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

	}

}
