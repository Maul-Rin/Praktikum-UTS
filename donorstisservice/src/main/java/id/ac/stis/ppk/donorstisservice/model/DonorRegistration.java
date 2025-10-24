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
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private DonorEvent event;

    @Column(nullable = false)
    private LocalDateTime tanggalPendaftaran = LocalDateTime.now();

    @Column(nullable = false)
    private String statusPendaftaran; // PENDING, DITERIMA, DITOLAK

    // Data Kuesioner Awal
    private Integer beratBadan;
    private LocalDate donorTerakhir;
    private String riwayatPenyakit;
    private Boolean apakahSedangHaid;

    // Status Final
    private String statusVerifikasiAkhir; // LULUS_DONOR, BATAL_DONOR, GAGAL_SKRINING

    // GANTI: Dari poinIpkmTerbit ke sistem sertifikat
    @Column(name = "sertifikat_diberikan")
    private Boolean sertifikatDiberikan = false;

    @Column(name = "nomor_sertifikat")
    private String nomorSertifikat;

    @Column(name = "tanggal_pemberian_sertifikat")
    private LocalDateTime tanggalPemberianSertifikat;
}