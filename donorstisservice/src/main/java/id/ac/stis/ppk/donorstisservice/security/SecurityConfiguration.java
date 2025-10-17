package id.ac.stis.ppk.donorstisservice.security;

import id.ac.stis.ppk.donorstisservice.security.jwt.AuthTokenFilter;
import id.ac.stis.ppk.donorstisservice.security.jwt.JwtUtils;
import id.ac.stis.ppk.donorstisservice.security.services.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Konfigurasi Utama Spring Security untuk JWT Token-Based Authentication.
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true) // Mengaktifkan anotasi @PreAuthorize
public class SecurityConfiguration {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtils jwtUtils;

    public SecurityConfiguration(UserDetailsServiceImpl userDetailsService, JwtUtils jwtUtils) {
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Bean untuk mengintegrasikan AuthTokenFilter ke dalam filter chain Spring Security.
     */
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter(jwtUtils, userDetailsService);
    }

    /**
     * Bean untuk menyediakan UserDetailsService dan PasswordEncoder kepada AuthenticationManager.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Bean untuk mendapatkan AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Bean untuk PasswordEncoder (Wajib: menggunakan BCrypt untuk hashing).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Konfigurasi Security Filter Chain.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Menonaktifkan CSRF (Cross-Site Request Forgery) karena menggunakan REST API/JWT
        http.csrf(AbstractHttpConfigurer::disable)

                // Konfigurasi Session untuk Stateless (Penting untuk JWT)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Konfigurasi otorisasi permintaan HTTP (path-based)
                .authorizeHttpRequests(auth -> auth
                        // Endpoint Public (Registrasi, Login)
                        .requestMatchers("/api/auth/**").permitAll()

                        // Endpoint Dokumentasi Swagger (Wajib Public untuk pengujian)
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Endpoint Admin KSR (Dilindungi oleh RBAC)
                        // Catatan: RBAC utama akan diterapkan melalui @PreAuthorize di Controller
                        .requestMatchers("/api/admin/**").hasRole("ADMIN_KSR")

                        // Semua endpoint lain harus terotentikasi
                        .anyRequest().authenticated()
                );

        // Terapkan Authentication Provider
        http.authenticationProvider(authenticationProvider());

        // Tambahkan JWT Filter sebelum filter default Spring Security
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}