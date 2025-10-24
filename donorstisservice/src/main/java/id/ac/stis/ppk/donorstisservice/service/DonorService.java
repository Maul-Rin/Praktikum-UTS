package id.ac.stis.ppk.donorstisservice.service;

import id.ac.stis.ppk.donorstisservice.exception.ResourceNotFoundException;
import id.ac.stis.ppk.donorstisservice.exception.QuotaExceededException;
import id.ac.stis.ppk.donorstisservice.model.DonorEvent;
import id.ac.stis.ppk.donorstisservice.model.DonorRegistration;
import id.ac.stis.ppk.donorstisservice.model.User;
import id.ac.stis.ppk.donorstisservice.payload.request.RegistrationRequest;
import id.ac.stis.ppk.donorstisservice.repository.DonorEventRepository;
import id.ac.stis.ppk.donorstisservice.repository.DonorRegistrationRepository;
import id.ac.stis.ppk.donorstisservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class DonorService {

    private final DonorEventRepository eventRepository;
    private final DonorRegistrationRepository registrationRepository;
    private final UserRepository userRepository;

    public DonorService(DonorEventRepository eventRepository, DonorRegistrationRepository registrationRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public DonorRegistration registerDonor(Long eventId, Long userId, RegistrationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan."));

        DonorEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Jadwal donor tidak ditemukan."));

        if (!"TERBUKA".equalsIgnoreCase(event.getStatus())) {
            throw new IllegalStateException("Pendaftaran donor untuk event ini sudah ditutup.");
        }

        if (registrationRepository.existsByUserIdAndEventId(userId, eventId)) {
            throw new IllegalStateException("Anda sudah terdaftar pada kegiatan donor ini.");
        }

        long currentRegistered = registrationRepository.countByEventIdAndStatusPendaftaran(eventId, "PENDING")
                + registrationRepository.countByEventIdAndStatusPendaftaran(eventId, "DITERIMA");

        if (currentRegistered >= event.getKuotaMaksimal()) {
            throw new QuotaExceededException("Kuota pendaftar donor sudah penuh (" + event.getKuotaMaksimal() + " orang).");
        }

        if (request.getBeratBadan() < 45) {
            throw new IllegalStateException("Berat badan Anda (" + request.getBeratBadan() + " kg) kurang dari syarat minimal 45 kg.");
        }
        if (request.getDonorTerakhir() != null && request.getDonorTerakhir().isAfter(LocalDate.now().minusMonths(3))) {
            throw new IllegalStateException("Interval donor terakhir Anda kurang dari 3 bulan.");
        }

        DonorRegistration registration = new DonorRegistration();
        registration.setUser(user);
        registration.setEvent(event);
        registration.setBeratBadan(request.getBeratBadan());
        registration.setDonorTerakhir(request.getDonorTerakhir());
        registration.setRiwayatPenyakit(request.getRiwayatPenyakit());
        registration.setApakahSedangHaid(request.getApakahSedangHaid());
        registration.setStatusPendaftaran("PENDING");

        return registrationRepository.save(registration);
    }

    public List<DonorRegistration> getMyRegistrations(Long userId) {
        return registrationRepository.findByUserId(userId);
    }

    public DonorEvent createEvent(DonorEvent event) {
        event.setStatus("TERBUKA");
        return eventRepository.save(event);
    }

    public List<DonorEvent> getAllEvents() {
        return eventRepository.findAll();
    }

    @Transactional
    public DonorEvent updateEventStatus(Long eventId, String status) {
        DonorEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Jadwal donor tidak ditemukan."));

        event.setStatus(status.toUpperCase());
        return eventRepository.save(event);
    }

    @Transactional
    public DonorRegistration verifyRegistration(Long registrationId, String newStatus) {
        DonorRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Pendaftaran tidak ditemukan."));

        if (!"DITERIMA".equalsIgnoreCase(newStatus) && !"DITOLAK".equalsIgnoreCase(newStatus)) {
            throw new IllegalArgumentException("Status verifikasi tidak valid.");
        }

        registration.setStatusPendaftaran(newStatus.toUpperCase());
        return registrationRepository.save(registration);
    }

    @Transactional
    public DonorRegistration finalizeRegistration(Long registrationId, String finalStatus) {
        DonorRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Pendaftaran tidak ditemukan."));

        if (!"LULUS_DONOR".equalsIgnoreCase(finalStatus) && !"BATAL_DONOR".equalsIgnoreCase(finalStatus)) {
            throw new IllegalArgumentException("Status final tidak valid.");
        }

        registration.setStatusVerifikasiAkhir(finalStatus.toUpperCase());

        // PERUBAHAN: Tidak langsung set poin IPKM, tapi siapkan untuk pemberian sertifikat
        // Sertifikat akan diberikan melalui endpoint terpisah oleh Admin KSR

        return registrationRepository.save(registration);
    }
}