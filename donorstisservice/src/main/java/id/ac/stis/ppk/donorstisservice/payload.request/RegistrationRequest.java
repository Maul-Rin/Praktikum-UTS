package id.ac.stis.ppk.donorstisservice.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

@Data
public class RegistrationRequest {

    @Min(value = 30, message = "Berat badan minimal 30 kg.")
    private Integer beratBadan;

    private LocalDate donorTerakhir;

    @NotBlank
    private String riwayatPenyakit;

    private Boolean apakahSedangHaid;
}