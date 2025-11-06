package mw.maulidi.money_manager_springboot_starter_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import mw.maulidi.money_manager_springboot_starter_api.utils.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Marks this class as a Spring-managed bean (so Spring can detect and use it)
@RequiredArgsConstructor // Automatically generates a constructor for final fields (userDetailsService, jwtUtil)
public class JWTRequestFilter extends OncePerRequestFilter {

    // Used to load user information from the database based on the username (email)
    private final UserDetailsService userDetailsService;

    // Utility class that handles JWT generation, extraction, and validation
    private final JwtUtil jwtUtil;

    /**
     * This method runs once for every HTTP request.
     * It checks for a JWT token in the Authorization header and authenticates the user if the token is valid.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Extract the Authorization header (should contain something like "Bearer <token>")
        final String authorizationHeader = request.getHeader("Authorization");

        String email = null; // Will hold the username/email extracted from the token
        String jwt = null;   // Will hold the actual JWT string

        // Check if header is present and starts with "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extract the token by removing "Bearer " (first 7 characters)
            jwt = authorizationHeader.substring(7);
            // Extract the username/email from the token using JwtUtil
            email = jwtUtil.extractUsername(jwt);
        }

        // Proceed only if we found an email and the user is not already authenticated
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load the user details from the database (via the UserDetailsService)
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // Validate the token using the JwtUtil
            // This checks if the token is correctly signed and not expired
            if (jwtUtil.validateToken(jwt, userDetails)) {
                // If valid, create an authentication object recognized by Spring Security
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, // Principal (user)
                                null, // No credentials (password not needed)
                                userDetails.getAuthorities() // Roles/permissions
                        );

                // Attach request details (like IP, session info) to the authentication
                authenticationToken.setDetails(new WebAuthenticationDetails(request));

                // Set the authentication in the SecurityContext so the user is now "logged in"
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // Continue the filter chain — pass the request to the next filter (or controller)
        filterChain.doFilter(request, response);
    }
}
