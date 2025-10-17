package id.ac.stis.ppk.donorstisservice.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data // Lombok untuk Getter, Setter, dll.
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; // Digunakan sebagai login ID (bisa email atau nomor unik)

    @Column(nullable = false)
    private String password;

    private String namaLengkap;

    @Column(unique = true, nullable = false)
    private String nomorIdentitas; // NPM atau NIP/NIDN

    @Column(nullable = false)
    private String jenisPengguna; // MAHASISWA, DOSEN, PEGAWAI

    private String email;
    private String nomorHp;

    @Enumerated(EnumType.STRING)
    private Role role;

    // --- Implementasi UserDetails (Wajib untuk Spring Security) ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return username;
    }

    // Perhatikan: Getter untuk password sudah disediakan oleh @Data Lombok

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}