package co.edu.javeriana.as.personapp.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for phone operations")
public class PhoneRequest {
    @Schema(description = "Phone number", example = "3001234567", required = true)
    private String number;
    
    @Schema(description = "Phone company", example = "CLARO", required = true)
    private String company;
    
    @Schema(description = "Person ID (CC)", example = "1001", required = true)
    private String personId;
    
    @Schema(description = "Target database (MARIA or MONGO)", example = "MARIA", required = true)
    private String database;
} 