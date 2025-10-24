package id.ac.stis.ppk.donorstisservice.controller;

import id.ac.stis.ppk.donorstisservice.model.DonorRegistration;
import id.ac.stis.ppk.donorstisservice.payload.response.MessageResponse;
import id.ac.stis.ppk.donorstisservice.service.SertifikatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin/sertifikat")
@RequiredArgsConstructor
public class SertifikatController {

    private final SertifikatService sertifikatService;

    /**
     * Memberikan sertifikat kepada satu peserta
     * POST /api/admin/sertifikat/berikan/{registrationId}
     */
    @PostMapping("/berikan/{registrationId}")
    @PreAuthorize("hasRole('ADMIN_KSR')")
    public ResponseEntity<?> berikanSertifikat(@PathVariable Long registrationId) {
        try {
            DonorRegistration registration = sertifikatService.berikanSertifikat(registrationId);
            return ResponseEntity.ok(new MessageResponse(
                    "Sertifikat berhasil diberikan dengan nomor: " + registration.getNomorSertifikat()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Memberikan sertifikat ke multiple peserta sekaligus (batch)
     * POST /api/admin/sertifikat/berikan-batch
     * Body: { "registrationIds": [1, 2, 3] }
     */
    @PostMapping("/berikan-batch")
    @PreAuthorize("hasRole('ADMIN_KSR')")
    public ResponseEntity<?> berikanSertifikatBatch(@RequestBody List<Long> registrationIds) {
        try {
            List<DonorRegistration> registrations = sertifikatService.berikanSertifikatBatch(registrationIds);
            return ResponseEntity.ok(new MessageResponse(
                    "Berhasil memberikan " + registrations.size() + " sertifikat"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Mendapatkan daftar penerima sertifikat per event
     * GET /api/admin/sertifikat/penerima/{eventId}
     */
    @GetMapping("/penerima/{eventId}")
    @PreAuthorize("hasRole('ADMIN_KSR')")
    public ResponseEntity<List<DonorRegistration>> getDaftarPenerimaSertifikat(@PathVariable Long eventId) {
        List<DonorRegistration> penerima = sertifikatService.getDaftarPenerimaSertitikat(eventId);
        return ResponseEntity.ok(penerima);
    }
}