package id.ac.stis.ppk.donorstisservice.controller;

import id.ac.stis.ppk.donorstisservice.model.Role;
import id.ac.stis.ppk.donorstisservice.model.User;
import id.ac.stis.ppk.donorstisservice.payload.request.LoginRequest;
import id.ac.stis.ppk.donorstisservice.payload.request.SignupRequest;
import id.ac.stis.ppk.donorstisservice.payload.response.JwtResponse;
import id.ac.stis.ppk.donorstisservice.payload.response.MessageResponse;
import id.ac.stis.ppk.donorstisservice.repository.UserRepository;
import id.ac.stis.ppk.donorstisservice.security.jwt.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder encoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Endpoint untuk Login dan mendapatkan JWT Token.
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User userDetails = (User) authentication.getPrincipal();

        // Generate Token
        String jwt = jwtUtils.generateJwtToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getRole().name()));
    }

    /**
     * Endpoint untuk Registrasi User Baru.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        // Cek duplikasi username
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username sudah digunakan!"));
        }

        // Cek duplikasi Nomor Identitas (NPM/NIP)
        if (userRepository.existsByNomorIdentitas(signUpRequest.getNomorIdentitas())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Nomor Identitas sudah terdaftar!"));
        }

        // Buat Akun User Baru
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setNamaLengkap(signUpRequest.getNamaLengkap());
        user.setNomorIdentitas(signUpRequest.getNomorIdentitas());
        user.setEmail(signUpRequest.getEmail());
        user.setNomorHp(signUpRequest.getNomorHp());
        user.setJenisPengguna(signUpRequest.getJenisPengguna().toUpperCase());

        // Default Role: Set ROLE_USER untuk semua yang mendaftar
        user.setRole(Role.ROLE_USER);

        // Khusus untuk kemudahan testing, Anda bisa menambahkan logika inisiasi Admin KSR di awal:
        // if (signUpRequest.getUsername().equals("admin.ksr")) {
        //     user.setRole(Role.ROLE_ADMIN_KSR);
        // } else {
        //     user.setRole(Role.ROLE_USER);
        // }


        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User berhasil terdaftar!"));
    }
}