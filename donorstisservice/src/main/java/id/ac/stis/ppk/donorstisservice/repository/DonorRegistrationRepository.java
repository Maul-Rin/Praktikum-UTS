package id.ac.stis.ppk.donorstisservice.repository;

import id.ac.stis.ppk.donorstisservice.model.DonorRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DonorRegistrationRepository extends JpaRepository<DonorRegistration, Long> {

    List<DonorRegistration> findByUserId(Long userId);

    // Method untuk Cek Kuota: Menghitung pendaftar dengan status tertentu
    long countByEventIdAndStatusPendaftaran(Long eventId, String status);

    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    // PERBAIKAN: Method untuk mendapatkan semua pendaftar berdasarkan Event ID
    List<DonorRegistration> findByEventId(Long eventId);
}