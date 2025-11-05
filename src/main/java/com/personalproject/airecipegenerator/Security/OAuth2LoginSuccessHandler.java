package com.personalproject.airecipegenerator.Security;

import com.personalproject.airecipegenerator.Dao.User;
import com.personalproject.airecipegenerator.Repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JpaUserDetailsService userDetailsService;


    public OAuth2LoginSuccessHandler(JwtService jwtService,
                                     UserRepository userRepository,
                                     PasswordEncoder passwordEncoder,
                                     JpaUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauthUser = oauthToken.getPrincipal();
        Map<String, Object> attributes = oauthUser.getAttributes();

        // Extract email. This varies by provider.
        String email = null;
        String provider = oauthToken.getAuthorizedClientRegistrationId();

        if ("google".equals(provider)) {
            email = (String) attributes.get("email");
        } else if ("facebook".equals(provider)) {
            email = (String) attributes.get("email");
            if (email == null) {
                email = (String) attributes.get("login") + "@facebook.com"; // Fallback
            }
        }

        if (email == null) {
            throw new ServletException("Could not find email from OAuth2 provider.");
        }

        // --- Find or Create User ---
        // Find existing user or create a new one
        String finalEmail = email;
        User user = userRepository.findByUsername(email)
                .orElseGet(() -> {
                    // Create a new user for this social login
                    User newUser = new User();
                    newUser.setUsername(finalEmail);
                    // Create a random, unusable password
                    String randomPassword = passwordEncoder.encode(UUID.randomUUID().toString());
                    newUser.setPassword(randomPassword);
                    return userRepository.save(newUser);
                });
        // load the UserDetails for our local user to generate the token
        var userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String jwt = jwtService.generateToken(userDetails);

        // Sends the user back to the app, with the token in the URL.
        String redirectUrl = "http://localhost:8080/index.html?token=" + jwt;
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}