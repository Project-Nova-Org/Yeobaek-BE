package com.nova.yeobaek.global.config;

import java.util.List;

import com.nova.yeobaek.global.auth.jwt.JwtAuthFilter;
import com.nova.yeobaek.global.auth.oauth.handler.OAuth2LoginSuccessHandler;
import com.nova.yeobaek.global.auth.oauth.service.CustomOAuth2UserService;
import com.nova.yeobaek.global.auth.oauth.service.CustomOidcUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthFilter jwtAuthFilter;
	private final OAuth2LoginSuccessHandler successHandler;
	private final CustomOidcUserService customOidcUserService;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final AuthenticationEntryPoint jwtAuthenticationEntryPoint;

	private static final String[] PERMIT_URLS = {
			"/",
			"/health",
			"/swagger-ui/**",
			"/v3/api-docs/**",
			"/oauth2/authorization/**",
			"/login/oauth2/code/**",
			"/api/auth/reissue",
			"/api/auth/logout"
	};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint(jwtAuthenticationEntryPoint)
				)
				.oauth2Login(oauth -> oauth
						.userInfoEndpoint(userInfo -> userInfo
								.userService(customOAuth2UserService)
								.oidcUserService(customOidcUserService)
						)
						.successHandler(successHandler)
				)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PERMIT_URLS).permitAll()
                        .anyRequest().authenticated()
                )
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
		configuration.setAllowedOrigins(List.of(
				// 로컬 back, front
				"http://localhost:8080",
				"http://localhost:8081"
				// 배포 back, front
				// "{배포url}"
		)); // backend, frontend (로컬, 배포) Origins 추가

        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
