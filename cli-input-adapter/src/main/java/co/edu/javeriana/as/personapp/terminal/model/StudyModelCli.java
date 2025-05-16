package co.edu.javeriana.as.personapp.terminal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyModelCli {
    private Integer personId;
    private Integer professionId;
    private String graduationDate; // String for simplicity in CLI, can be parsed as needed
    private String universityName;
    private String database;
} 