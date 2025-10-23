package id.ac.stis.ppk.donorstisservice.security.jwt;

import id.ac.stis.ppk.donorstisservice.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    public AuthTokenFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            logger.info("=== JWT Filter ===");
            logger.info("Request URI: {}", request.getRequestURI());
            logger.info("JWT Token: {}", jwt != null ? "Present" : "Missing");

            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                logger.info("Username from token: {}", username);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                logger.info("User authorities: {}", userDetails.getAuthorities());

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                logger.info("Authentication set successfully");
            } else {
                logger.warn("JWT validation failed or token missing");
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            String token = headerAuth.substring(7);

            // DEBUG: Log detail token
            logger.info("=== TOKEN DEBUG ===");
            logger.info("Full Authorization header: [{}]", headerAuth);
            logger.info("Original token length: {}", token.length());

            // FIX: Hapus double "Bearer" jika ada (masalah dari Swagger/Postman)
            if (token.startsWith("Bearer ")) {
                logger.warn("DOUBLE 'Bearer' detected! Removing extra 'Bearer'");
                token = token.substring(7);
            }

            // FIX: Hapus semua whitespace (spasi, tab, newline)
            if (token.contains(" ") || token.contains("\t") || token.contains("\n") || token.contains("\r")) {
                logger.warn("Whitespace detected! Cleaning token...");
                token = token.replaceAll("\\s+", "");
            }

            logger.info("Final cleaned token length: {}", token.length());

            return token;
        }

        return null;
    }
}