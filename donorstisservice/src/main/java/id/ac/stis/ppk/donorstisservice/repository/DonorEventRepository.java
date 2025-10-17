package id.ac.stis.ppk.donorstisservice.repository;

import id.ac.stis.ppk.donorstisservice.model.DonorEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonorEventRepository extends JpaRepository<DonorEvent, Long> {
}