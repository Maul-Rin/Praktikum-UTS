package id.ac.stis.ppk.donorstisservice.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {

    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    @NotBlank
    private String namaLengkap;

    @NotBlank
    private String nomorIdentitas; // NPM atau NIP/NIDN

    // Harus diisi salah satu dari MAHASISWA, DOSEN, atau PEGAWAI
    @NotBlank
    private String jenisPengguna;

    @Email
    private String email;

    private String nomorHp;

    // Catatan: Role akan di-set default di service, tidak perlu di request
}