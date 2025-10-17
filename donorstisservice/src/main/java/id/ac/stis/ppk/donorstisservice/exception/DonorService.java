// Tambahkan import exception di DonorService
// import id.ac.stis.ppk.donorstisservice.exception.ResourceNotFoundException;
// import id.ac.stis.ppk.donorstisservice.exception.QuotaExceededException;

// ... (Di dalam class DonorService)

// Metode untuk ADMIN KSR: Mendapatkan semua event
public List<DonorEvent> getAllEvents() {
    return eventRepository.findAll();
}

// Metode untuk ADMIN KSR: Update Status Event
@Transactional
public DonorEvent updateEventStatus(Long eventId, String status) {
    DonorEvent event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Jadwal donor tidak ditemukan."));

    event.setStatus(status.toUpperCase());
    return eventRepository.save(event);
}

// Metode untuk ADMIN KSR: Finalisasi Status
@Transactional
public DonorRegistration finalizeRegistration(Long registrationId, String finalStatus) {
    DonorRegistration registration = registrationRepository.findById(registrationId)
            .orElseThrow(() -> new ResourceNotFoundException("Pendaftaran tidak ditemukan."));

    if (!"LULUS_DONOR".equalsIgnoreCase(finalStatus) && !"BATAL_DONOR".equalsIgnoreCase(finalStatus)) {
        throw new IllegalArgumentException("Status final tidak valid.");
    }

    registration.setStatusVerifikasiAkhir(finalStatus.toUpperCase());

    // Logika IPKM: Jika LULUS_DONOR, set poinIpkmTerbit menjadi true
    if ("LULUS_DONOR".equalsIgnoreCase(finalStatus)) {
        registration.setPoinIpkmTerbit(true);
    }

    return registrationRepository.save(registration);
}