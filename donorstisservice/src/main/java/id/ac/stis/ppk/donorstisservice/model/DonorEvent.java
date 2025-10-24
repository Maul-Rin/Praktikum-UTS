package id.ac.stis.ppk.donorstisservice.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "donor_events")
@Data
public class DonorEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String namaKegiatan;

    @Column(nullable = false)
    private LocalDate tanggalMulai;

    private LocalDate tanggalSelesai;

    @Column(nullable = false)
    private String lokasi;

    @Column(nullable = false)
    private Integer kuotaMaksimal;

    @Column(nullable = false)
    private String status;

    private Long createdByUserId;

    // TAMBAHAN: Info sertifikat untuk event ini
    @Column(name = "sertifikat_tersedia")
    private Boolean sertifikatTersedia = true;

    @Column(name = "template_sertifikat")
    private String templateSertifikat;
}