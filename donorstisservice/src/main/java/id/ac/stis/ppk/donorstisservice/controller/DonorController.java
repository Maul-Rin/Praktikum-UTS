package id.ac.stis.ppk.donorstisservice.controller;

import id.ac.stis.ppk.donorstisservice.model.DonorEvent;
import id.ac.stis.ppk.donorstisservice.model.DonorRegistration;
import id.ac.stis.ppk.donorstisservice.model.User;
import id.ac.stis.ppk.donorstisservice.payload.request.RegistrationRequest;
import id.ac.stis.ppk.donorstisservice.payload.response.MessageResponse;
import id.ac.stis.ppk.donorstisservice.repository.DonorEventRepository;
import id.ac.stis.ppk.donorstisservice.service.DonorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/donor")
public class DonorController {

    private final DonorService donorService;
    private final DonorEventRepository eventRepository;

    public DonorController(DonorService donorService, DonorEventRepository eventRepository) {
        this.donorService = donorService;
        this.eventRepository = eventRepository;
    }

    // Metode bantuan untuk mendapatkan user yang sedang login
    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // --------------------------------------------------------------------------
    // A. Melihat Jadwal (Bisa Diakses oleh USER dan Guest, tapi kita batasi terotentikasi)
    // --------------------------------------------------------------------------

    // GET /api/donor/events : Melihat jadwal donor yang TERBUKA
    // Note: Kita batasi USER saja yang bisa lihat, sesuai konfigurasi di SecurityConfiguration.
    @GetMapping("/events")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN_KSR')")
    public List<DonorEvent> getOpenEvents() {
        return eventRepository.findAll().stream()
                .filter(e -> "TERBUKA".equalsIgnoreCase(e.getStatus()))
                .collect(Collectors.toList());
    }

    // --------------------------------------------------------------------------
    // B. Pendaftaran Donor (Wajib Token)
    // --------------------------------------------------------------------------

    // POST /api/donor/register/{eventId} : Mendaftar ke sebuah event
    @PostMapping("/register/{eventId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> registerToEvent(@PathVariable Long eventId, @Valid @RequestBody RegistrationRequest request) {
        try {
            User currentUser = getAuthenticatedUser();
            donorService.registerDonor(eventId, currentUser.getId(), request);
            return ResponseEntity.ok(new MessageResponse("Pendaftaran donor berhasil! Menunggu verifikasi KSR."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Pendaftaran Gagal: " + e.getMessage()));
        }
    }

    // GET /api/donor/my-registrations : Melihat riwayat pendaftaran user yang login
    @GetMapping("/my-registrations")
    @PreAuthorize("hasRole('USER')")
    public List<DonorRegistration> getMyRegistrations() {
        User currentUser = getAuthenticatedUser();
        return donorService.getMyRegistrations(currentUser.getId());
    }
}