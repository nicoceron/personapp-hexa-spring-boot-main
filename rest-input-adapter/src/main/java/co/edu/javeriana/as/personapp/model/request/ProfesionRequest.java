package co.edu.javeriana.as.personapp.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for profession operations")
public class ProfesionRequest {
    @Schema(description = "Profession ID", example = "1")
    private String identification; // Will be parsed to Integer in adapter/mapper
    
    @Schema(description = "Profession name", example = "Software Engineer")
    private String name;
    
    @Schema(description = "Profession description", example = "Designs and develops software applications")
    private String description;
    
    @Schema(description = "Target database (MARIA or MONGO)", example = "MARIA")
    private String database; // To indicate target DB (MARIA or MONGO)
} 