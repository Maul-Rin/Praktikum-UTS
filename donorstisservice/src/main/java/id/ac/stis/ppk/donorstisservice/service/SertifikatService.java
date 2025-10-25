package id.ac.stis.ppk.donorstisservice.service;

import id.ac.stis.ppk.donorstisservice.exception.ResourceNotFoundException;
import id.ac.stis.ppk.donorstisservice.model.DonorRegistration;
import id.ac.stis.ppk.donorstisservice.repository.DonorRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SertifikatService {

    private final DonorRegistrationRepository registrationRepository;

    /**
     * Memberikan sertifikat kepada peserta yang sudah LULUS_DONOR
     */
    @Transactional
    public DonorRegistration berikanSertifikat(Long registrationId) {
        DonorRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registrasi tidak ditemukan"));

        // Validasi: Hanya yang LULUS_DONOR yang dapat sertifikat
        if (!"LULUS_DONOR".equalsIgnoreCase(registration.getStatusVerifikasiAkhir())) {
            throw new IllegalStateException("Hanya peserta dengan status LULUS_DONOR yang dapat menerima sertifikat");
        }

        // Validasi: Sertifikat belum diberikan
        if (Boolean.TRUE.equals(registration.getSertifikatDiberikan())) {
            throw new IllegalStateException("Sertifikat sudah pernah diberikan untuk registrasi ini");
        }

        // Generate nomor sertifikat unik
        String nomorSertifikat = generateNomorSertifikat(registration);

        // Update registration
        registration.setSertifikatDiberikan(true);
        registration.setNomorSertifikat(nomorSertifikat);
        registration.setTanggalPemberianSertifikat(LocalDateTime.now());

        return registrationRepository.save(registration);
    }

    /**
     * Memberikan sertifikat ke multiple registrations sekaligus (batch)
     */
    @Transactional
    public List<DonorRegistration> berikanSertifikatBatch(List<Long> registrationIds) {
        return registrationIds.stream()
                .map(this::berikanSertifikat)
                .toList();
    }

    /**
     * Generate nomor sertifikat unik
     * Format: CERT-STIS-{YEAR}-{MONTH}-{RANDOM}
     */
    private String generateNomorSertifikat(DonorRegistration registration) {
        String year = String.valueOf(LocalDateTime.now().getYear());
        String month = String.format("%02d", LocalDateTime.now().getMonthValue());
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return String.format("CERT-STIS-%s-%s-%s", year, month, random);
    }

    /**
     * Mendapatkan daftar peserta yang sudah menerima sertifikat
     */
    public List<DonorRegistration> getDaftarPenerimaSertitikat(Long eventId) {
        return registrationRepository.findByEventId(eventId).stream()
                .filter(reg -> Boolean.TRUE.equals(reg.getSertifikatDiberikan()))
                .toList();
    }
}