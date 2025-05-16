package co.edu.javeriana.as.personapp.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object for study operations")
public class StudyResponse {
    @Schema(description = "Person ID (CC)", example = "1001")
    private String personId;

    @Schema(description = "Profession ID", example = "1")
    private String professionId;

    @Schema(description = "Graduation date (YYYY-MM-DD)", example = "2023-11-20")
    private String graduationDate;

    @Schema(description = "University name", example = "Universidad Javeriana")
    private String universityName;

    @Schema(description = "Source database (MARIA or MONGO)", example = "MARIA")
    private String database;

    @Schema(description = "Status of the operation", example = "OK")
    private String status;

    public StudyResponse(String personId, String professionId, String database, String status) {
        this.personId = personId;
        this.professionId = professionId;
        this.database = database;
        this.status = status;
    }
} 