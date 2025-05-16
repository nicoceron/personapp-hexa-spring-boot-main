package co.edu.javeriana.as.personapp.terminal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelefonoModelCli {
    private String number;
    private String company;
    private Integer ownerId; // Just the ID for CLI display simplicity
    private String database; // To indicate which DB this model is associated with
} 