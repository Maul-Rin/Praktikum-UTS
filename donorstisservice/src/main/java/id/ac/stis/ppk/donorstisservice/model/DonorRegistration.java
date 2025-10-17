package id.ac.stis.ppk.donorstisservice.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "donor_registrations")
@Data
public class DonorRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // FK ke User

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private DonorEvent event; // FK ke DonorEvent

    @Column(nullable = false)
    private LocalDateTime tanggalPendaftaran = LocalDateTime.now();

    @Column(nullable = false)
    private String statusPendaftaran; // PENDING, DITERIMA, DITOLAK (Verifikasi Awal)

    // Data Kuesioner Awal (Untuk Syarat Donor)
    private Integer beratBadan; // Minimal 45 kg
    private LocalDate donorTerakhir; // Interval donor, minimal 3 bulan
    private String riwayatPenyakit; // JAWABAN: ADA/TIDAK
    private Boolean apakahSedangHaid; // Khusus wanita

    // Status Final (Setelah Hari-H Donor)
    private String statusVerifikasiAkhir; // LULUS_DONOR, BATAL_DONOR, GAGAL_SKRINING
    private Boolean poinIpkmTerbit = false;
}