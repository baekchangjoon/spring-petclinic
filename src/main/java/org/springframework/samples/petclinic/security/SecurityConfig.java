package org.springframework.samples.petclinic.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.csrf(csrf -> csrf.disable())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(authz -> authz.requestMatchers("/api/auth/**")
				.permitAll()
				.requestMatchers("/swagger-ui/**")
				.permitAll()
				.requestMatchers("/v3/api-docs/**")
				.permitAll()
				.requestMatchers("/swagger-resources/**")
				.permitAll()
				.requestMatchers("/webjars/**")
				.permitAll()
				.requestMatchers("/actuator/**")
				.permitAll()
				.requestMatchers("/")
				.permitAll()
				.requestMatchers("/oups")
				.permitAll()
				.requestMatchers("/resources/**")
				.permitAll()
				.requestMatchers("/static/**")
				.permitAll()
				.requestMatchers("/css/**")
				.permitAll()
				.requestMatchers("/js/**")
				.permitAll()
				.requestMatchers("/images/**")
				.permitAll()
				.requestMatchers("/webjars/**")
				.permitAll()
				.anyRequest()
				.authenticated())
			.addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userDetailsService),
					UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}
