package id.ac.stis.ppk.donorstisservice.controller;

import id.ac.stis.ppk.donorstisservice.model.User;
import id.ac.stis.ppk.donorstisservice.payload.request.ChangePasswordRequest;
import id.ac.stis.ppk.donorstisservice.payload.request.EditProfileRequest;
import id.ac.stis.ppk.donorstisservice.payload.response.MessageResponse;
import id.ac.stis.ppk.donorstisservice.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
// Semua endpoint di sini membutuhkan otentikasi (sudah dikonfigurasi di SecurityConfiguration)
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Metode bantuan untuk mendapatkan user yang sedang login
    private Optional<User> getAuthenticatedUser() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User userDetails) {
            return userRepository.findById(userDetails.getId());
        }
        return Optional.empty();
    }

    // --------------------------------------------------------------------------
    // 1. Endpoint Profil (GET /api/user/profile) - Wajib Soal No. 2
    // --------------------------------------------------------------------------
    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN_KSR')")
    public ResponseEntity<?> getProfile() {
        Optional<User> user = getAuthenticatedUser();
        if (user.isPresent()) {
            // Hilangkan password sebelum dikirim
            user.get().setPassword(null);
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.status(404).body(new MessageResponse("User tidak ditemukan atau tidak terotentikasi."));
    }

    // --------------------------------------------------------------------------
    // 2. Endpoint Edit Profil (PUT /api/user/profile) - Wajib Soal No. 2
    // --------------------------------------------------------------------------
    @PutMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN_KSR')")
    public ResponseEntity<?> editProfile(@Valid @RequestBody EditProfileRequest request) {
        Optional<User> userOptional = getAuthenticatedUser();

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Logika Update data non-sensitif
            user.setNamaLengkap(request.getNamaLengkap());
            user.setEmail(request.getEmail());
            user.setNomorHp(request.getNomorHp());

            userRepository.save(user);
            return ResponseEntity.ok(new MessageResponse("Profil berhasil diperbarui!"));
        }
        return ResponseEntity.status(401).body(new MessageResponse("Otentikasi gagal."));
    }

    // --------------------------------------------------------------------------
    // 3. Endpoint Ganti Password (PUT /api/user/change-password) - Wajib Soal No. 2
    // --------------------------------------------------------------------------
    @PutMapping("/change-password")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN_KSR')")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Optional<User> userOptional = getAuthenticatedUser();

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Verifikasi Password Lama
            if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Password lama tidak sesuai."));
            }

            // Simpan Password Baru (setelah di-encode)
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            return ResponseEntity.ok(new MessageResponse("Password berhasil diubah. Silakan login kembali!"));
        }
        return ResponseEntity.status(401).body(new MessageResponse("Otentikasi gagal."));
    }

    // --------------------------------------------------------------------------
    // 4. Endpoint Hapus Akun (DELETE /api/user/delete) - Wajib Soal No. 2
    // --------------------------------------------------------------------------
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN_KSR')")
    public ResponseEntity<?> deleteAccount() {
        Optional<User> userOptional = getAuthenticatedUser();

        if (userOptional.isPresent()) {
            userRepository.delete(userOptional.get());
            return ResponseEntity.ok(new MessageResponse("Akun berhasil dihapus. Sampai jumpa!"));
        }
        return ResponseEntity.status(401).body(new MessageResponse("Otentikasi gagal."));
    }
}