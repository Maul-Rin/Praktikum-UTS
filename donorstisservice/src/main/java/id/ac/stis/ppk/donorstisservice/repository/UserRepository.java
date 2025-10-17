package id.ac.stis.ppk.donorstisservice.repository;

import id.ac.stis.ppk.donorstisservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByNomorIdentitas(String nomorIdentitas);
}