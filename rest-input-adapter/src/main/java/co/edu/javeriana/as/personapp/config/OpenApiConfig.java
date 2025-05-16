package co.edu.javeriana.as.personapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI personAppOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PersonApp API")
                        .description("REST API for managing people, professions, phones and studies")
                        .version("v1.0"));
    }
} 