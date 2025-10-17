package id.ac.stis.ppk.donorstisservice.model;

/**
 * Enumerasi (Enum) untuk mendefinisikan peran (Role) pengguna dalam sistem.
 * Digunakan untuk otorisasi akses (Role-Based Access Control).
 */
public enum Role {
    /**
     * Peran untuk seluruh Civitas Akademika (Mahasiswa, Dosen, Pegawai).
     * Memiliki akses untuk mendaftar donor dan melihat profil/status pendaftaran sendiri.
     */
    ROLE_USER,

    /**
     * Peran untuk anggota UKM KSR/Panitia kegiatan donor.
     * Memiliki akses administratif (membuat jadwal, menentukan kuota, verifikasi pendaftar).
     */
    ROLE_ADMIN_KSR
}