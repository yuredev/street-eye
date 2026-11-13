package yuredev.street_eye_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// Informa que o Spring deve achar classes no pacote street_eye_video_delivery também,
// por padrão ele só olha nas suas subpastas
@SpringBootApplication(scanBasePackages = {
    "yuredev.street_eye_backend",
    "yuredev.street_eye_authentication",
    "yuredev.street_eye_video_delivery"
})
@EntityScan(basePackages = {
    "yuredev.street_eye_backend.models",
    "yuredev.street_eye_authentication.models"
})
@EnableJpaRepositories(basePackages = {
    "yuredev.street_eye_backend.repositories",
    "yuredev.street_eye_authentication.repositories"
})
public class StreetEyeBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(StreetEyeBackendApplication.class, args);
	}

}
