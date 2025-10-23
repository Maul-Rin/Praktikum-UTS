package id.ac.stis.ppk.donorstisservice.security;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "Bearer Authentication", // Nama skema yang akan digunakan
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@OpenAPIDefinition(
        info = @Info(
                title = "LAYANAN DONOR DARAH POLSTAT STIS API",
                version = "1.0",
                description = "Web Service untuk Manajemen Pendaftaran Donor Darah Civitas Akademika Polstat STIS. Dibuat untuk UTS PPK."
        ),
        security = {
                @SecurityRequirement(name = "Bearer Authentication") // Mengharuskan skema ini secara global
        }
)
public class OpenApiConfig {
    // Class ini hanya berisi anotasi konfigurasi Open API.
}
