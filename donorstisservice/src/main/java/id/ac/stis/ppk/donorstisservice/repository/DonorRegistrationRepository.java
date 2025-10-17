package id.ac.stis.ppk.donorstisservice.repository;

import id.ac.stis.ppk.donorstisservice.model.DonorRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DonorRegistrationRepository extends JpaRepository<DonorRegistration, Long> {
    List<DonorRegistration> findByUserId(Long userId);
    long countByEventIdAndStatusPendaftaran(Long eventId, String status);
    boolean existsByUserIdAndEventId(Long userId, Long eventId);
}