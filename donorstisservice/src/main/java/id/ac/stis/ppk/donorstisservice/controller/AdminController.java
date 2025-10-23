package id.ac.stis.ppk.donorstisservice.controller;

import id.ac.stis.ppk.donorstisservice.model.DonorEvent;
import id.ac.stis.ppk.donorstisservice.model.DonorRegistration;
import id.ac.stis.ppk.donorstisservice.payload.response.MessageResponse;
import id.ac.stis.ppk.donorstisservice.repository.DonorRegistrationRepository;
import id.ac.stis.ppk.donorstisservice.service.DonorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final DonorService donorService;
    private final DonorRegistrationRepository registrationRepository;

    public AdminController(DonorService donorService, DonorRegistrationRepository registrationRepository) {
        this.donorService = donorService;
        this.registrationRepository = registrationRepository;
    }

    // POST /api/admin/events: Membuat jadwal donor baru
    @PostMapping("/events")
    @PreAuthorize("hasRole('ADMIN_KSR')")
    public ResponseEntity<?> createEvent(@Valid @RequestBody DonorEvent donorEvent) {
        System.out.println("===== CREATE EVENT DIPANGGIL =====");
        System.out.println("User: " + SecurityContextHolder.getContext().getAuthentication().getName());
        System.out.println("Authorities: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());

        DonorEvent createdEvent = donorService.createEvent(donorEvent);
        return ResponseEntity.ok(createdEvent);
    }

    // GET /api/admin/events: Melihat semua jadwal
    @GetMapping("/events")
    @PreAuthorize("hasRole('ADMIN_KSR')")
    public List<DonorEvent> getAllEvents() {
        return donorService.getAllEvents();
    }

    // PUT /api/admin/events/{id}/status: Mengubah status event
    @PutMapping("/events/{id}/status")
    @PreAuthorize("hasRole('ADMIN_KSR')")
    public ResponseEntity<?> updateEventStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            DonorEvent updatedEvent = donorService.updateEventStatus(id, status);
            return ResponseEntity.ok(new MessageResponse("Status event berhasil diubah menjadi: " + updatedEvent.getStatus()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error update status: " + e.getMessage()));
        }
    }

    // GET /api/admin/registrations/event/{eventId}: Melihat daftar pendaftar per event
    @GetMapping("/registrations/event/{eventId}")
    @PreAuthorize("hasRole('ADMIN_KSR')")
    public List<DonorRegistration> getRegistrationsByEvent(@PathVariable Long eventId) {
        return registrationRepository.findByEventId(eventId);
    }

    // PUT /api/admin/registrations/{regId}/verify: Verifikasi awal
    @PutMapping("/registrations/{regId}/verify")
    @PreAuthorize("hasRole('ADMIN_KSR')")
    public ResponseEntity<?> verifyRegistration(@PathVariable Long regId, @RequestParam String status) {
        try {
            DonorRegistration reg = donorService.verifyRegistration(regId, status);
            return ResponseEntity.ok(new MessageResponse("Verifikasi pendaftaran berhasil diubah menjadi: " + reg.getStatusPendaftaran()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error verifikasi: " + e.getMessage()));
        }
    }

    // PUT /api/admin/registrations/{regId}/finalize: Finalisasi Status Donor
    @PutMapping("/registrations/{regId}/finalize")
    @PreAuthorize("hasRole('ADMIN_KSR')")
    public ResponseEntity<?> finalizeRegistration(@PathVariable Long regId, @RequestParam String status) {
        try {
            DonorRegistration reg = donorService.finalizeRegistration(regId, status);
            return ResponseEntity.ok(new MessageResponse("Status final donor berhasil diubah menjadi: " + reg.getStatusVerifikasiAkhir()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error finalisasi: " + e.getMessage()));
        }
    }
}