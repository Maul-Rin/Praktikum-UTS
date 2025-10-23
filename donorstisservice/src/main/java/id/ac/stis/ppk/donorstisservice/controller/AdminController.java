package id.ac.stis.ppk.donorstisservice.controller;

import id.ac.stis.ppk.donorstisservice.model.DonorEvent;
import id.ac.stis.ppk.donorstisservice.model.DonorRegistration;
import id.ac.stis.ppk.donorstisservice.payload.response.MessageResponse; // WAJIB: Import MessageResponse
import id.ac.stis.ppk.donorstisservice.repository.DonorRegistrationRepository;
import id.ac.stis.ppk.donorstisservice.service.DonorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN_KSR')") // RBAC: Hanya ADMIN KSR yang bisa akses
public class AdminController {

    private final DonorService donorService;
    private final DonorRegistrationRepository registrationRepository;

    // PERBAIKAN: Constructor Injection untuk autowiring DonorService
    public AdminController(DonorService donorService, DonorRegistrationRepository registrationRepository) {
        this.donorService = donorService;
        this.registrationRepository = registrationRepository;
    }

    // --------------------------------------------------------------------------
    // A. Manajemen Jadwal Donor (Event)
    // --------------------------------------------------------------------------

    // POST /api/admin/events: Membuat jadwal donor baru
    @PostMapping("/events")
    public ResponseEntity<?> createEvent(@Valid @RequestBody DonorEvent donorEvent) {
        // Asumsi: Event dikirim tanpa ID, ID digenerate otomatis
        DonorEvent createdEvent = donorService.createEvent(donorEvent);
        return ResponseEntity.ok(createdEvent);
    }

    // GET /api/admin/events: Melihat semua jadwal (termasuk yang ditutup)
    @GetMapping("/events")
    public List<DonorEvent> getAllEvents() {
        return donorService.getAllEvents();
    }

    // PUT /api/admin/events/{id}/status: Mengubah status event (Misal: DITUTUP)
    @PutMapping("/events/{id}/status")
    public ResponseEntity<?> updateEventStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            DonorEvent updatedEvent = donorService.updateEventStatus(id, status);
            return ResponseEntity.ok(new MessageResponse("Status event berhasil diubah menjadi: " + updatedEvent.getStatus()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error update status: " + e.getMessage()));
        }
    }

    // --------------------------------------------------------------------------
    // B. Manajemen Pendaftar & Verifikasi
    // --------------------------------------------------------------------------

    // GET /api/admin/registrations/event/{eventId}: Melihat daftar pendaftar per event
    @GetMapping("/registrations/event/{eventId}")
    public List<DonorRegistration> getRegistrationsByEvent(@PathVariable Long eventId) {
        // PERBAIKAN: Memanggil method findByEventId yang baru ditambahkan
        return registrationRepository.findByEventId(eventId);
    }

    // PUT /api/admin/registrations/{regId}/verify: Verifikasi awal (DITERIMA/DITOLAK)
    @PutMapping("/registrations/{regId}/verify")
    public ResponseEntity<?> verifyRegistration(@PathVariable Long regId, @RequestParam String status) {
        try {
            DonorRegistration reg = donorService.verifyRegistration(regId, status);
            return ResponseEntity.ok(new MessageResponse("Verifikasi pendaftaran berhasil diubah menjadi: " + reg.getStatusPendaftaran()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error verifikasi: " + e.getMessage()));
        }
    }

    // PUT /api/admin/registrations/{regId}/finalize: Finalisasi Status Donor & Poin IPKM
    @PutMapping("/registrations/{regId}/finalize")
    public ResponseEntity<?> finalizeRegistration(@PathVariable Long regId, @RequestParam String status) {
        try {
            DonorRegistration reg = donorService.finalizeRegistration(regId, status);
            return ResponseEntity.ok(new MessageResponse("Status final donor berhasil diubah menjadi: " + reg.getStatusVerifikasiAkhir()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error finalisasi: " + e.getMessage()));
        }
    }
}