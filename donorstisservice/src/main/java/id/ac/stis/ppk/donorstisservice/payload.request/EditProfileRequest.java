package id.ac.stis.ppk.donorstisservice.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EditProfileRequest {
    @NotBlank
    private String namaLengkap;

    @Email
    private String email;

    private String nomorHp;

    // Nomor Identitas (NPM/NIP) tidak boleh diubah melalui endpoint ini
}