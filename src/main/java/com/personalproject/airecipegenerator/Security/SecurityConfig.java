package com.personalproject.airecipegenerator.Security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    public SecurityConfig(JwtAuthFilter jwtAuthFilter, OAuth2LoginSuccessHandler  oAuth2LoginSuccessHandler) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for simple JWT auth
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Allow our public pages and APIs
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/*.png", "/*.jpeg", "/*.jpg", "/*.css", "/*.js").permitAll()
                        .requestMatchers("/", "/index.html", "/shopping-list.html", "/login.html", "/register.html").permitAll()
                        .requestMatchers("/api/auth/**").permitAll() // Auth endpoints
                        .requestMatchers("/api/v1/recipes/generate").permitAll() // Original recipe generator
                        .requestMatchers("/h2-console/**").permitAll() // H2 Database console
                        .requestMatchers("/oauth2/**").permitAll()

                        // Secure everything else
                        .requestMatchers("/api/v1/list/**").authenticated()
                        .anyRequest().authenticated()
                )
                // Fix for H2 console
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2->{oauth2.successHandler(oAuth2LoginSuccessHandler);}); // This adds your JWT filter to the chain;

        return http.build();
    }
}