package id.ac.stis.ppk.donorstisservice.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank
    private String oldPassword;

    @NotBlank
    @Size(min = 6, message = "Password baru minimal 6 karakter.")
    private String newPassword;
}