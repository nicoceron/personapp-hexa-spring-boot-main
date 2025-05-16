package co.edu.javeriana.as.personapp.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object for phone operations")
public class PhoneResponse {
    @Schema(description = "Phone number", example = "3001234567")
    private String number;

    @Schema(description = "Phone company", example = "CLARO")
    private String company;

    @Schema(description = "Person ID (CC)", example = "1001")
    private String personId;

    @Schema(description = "Source database (MARIA or MONGO)", example = "MARIA")
    private String database;

    @Schema(description = "Status of the operation", example = "OK")
    private String status;

    public PhoneResponse(String number, String personId, String database, String status) {
        this.number = number;
        this.personId = personId;
        this.database = database;
        this.status = status;
    }
} 