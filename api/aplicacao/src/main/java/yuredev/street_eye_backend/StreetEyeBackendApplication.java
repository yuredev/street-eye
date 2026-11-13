package yuredev.street_eye_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Informa que o Spring deve achar classes no pacote street_eye_video_delivery também,
// por padrão ele só olha nas suas subpastas
@SpringBootApplication(scanBasePackages = {
    "yuredev.street_eye_backend",
    "yuredev.street_eye_video_delivery"
})
public class StreetEyeBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(StreetEyeBackendApplication.class, args);
	}

}
