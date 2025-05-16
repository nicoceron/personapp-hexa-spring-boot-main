package co.edu.javeriana.as.personapp.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object for profession operations")
public class ProfesionResponse {
    @Schema(description = "Profession ID", example = "1")
    private String identification;
    
    @Schema(description = "Profession name", example = "Software Engineer")
    private String name;
    
    @Schema(description = "Profession description", example = "Designs and develops software applications")
    private String description;
    
    @Schema(description = "Source database (MARIA or MONGO)", example = "MARIA")
    private String database; // To indicate which DB this response is from
    
    @Schema(description = "Status of the operation", example = "OK")
    private String status; // For OK or ERROR messages

    // Constructor for error responses or simpler status updates
    public ProfesionResponse(String identification, String database, String status) {
        this.identification = identification;
        this.database = database;
        this.status = status;
    }
} 