package co.edu.javeriana.as.personapp.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for study operations")
public class StudyRequest {
    @Schema(description = "Person ID (CC)", example = "1001")
    private String personId; // Will be parsed to Integer

    @Schema(description = "Profession ID", example = "1")
    private String professionId; // Will be parsed to Integer

    @Schema(description = "Graduation date (YYYY-MM-DD)", example = "2023-11-20")
    private String graduationDate; // Will be parsed to LocalDate

    @Schema(description = "University name", example = "Universidad Javeriana")
    private String universityName;

    @Schema(description = "Target database (MARIA or MONGO)", example = "MARIA")
    private String database;
} 