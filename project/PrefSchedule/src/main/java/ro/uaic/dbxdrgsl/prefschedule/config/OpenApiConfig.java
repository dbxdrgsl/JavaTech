package ro.uaic.dbxdrgsl.prefschedule.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for the PrefSchedule API.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI prefScheduleOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PrefSchedule API")
                        .description("API for managing student course preference scheduling. " +
                                "Students can submit preferences for optional courses, and the system " +
                                "supports fair distribution using a stable-matching algorithm.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("UAIC Database and XML Group")
                                .email("dbxdrgsl@uaic.ro"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
