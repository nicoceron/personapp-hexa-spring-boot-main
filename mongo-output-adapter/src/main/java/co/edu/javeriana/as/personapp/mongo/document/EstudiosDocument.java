package co.edu.javeriana.as.personapp.mongo.document;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "estudios")
public class EstudiosDocument {
	@Id
	private String _id; // MongoDB default ID, can be composite key string like "personId_professionId"
	private Integer idProf; // Profession ID from original composite key
	private Integer ccPer;  // Person ID from original composite key
	private LocalDate fecha;
	private String univer;

	@DocumentReference(lazy = true)
	private PersonaDocument primaryPersona; // Reference to Person document

	@DocumentReference(lazy = true)
	private ProfesionDocument primaryProfesion; // Reference to Profession document

	// Constructor for easier creation with composite key parts
	public EstudiosDocument(Integer idProf, Integer ccPer, LocalDate fecha, String univer, PersonaDocument primaryPersona, ProfesionDocument primaryProfesion) {
		this._id = ccPer + "_" + idProf; // Example composite key for MongoDB's _id
		this.idProf = idProf;
		this.ccPer = ccPer;
		this.fecha = fecha;
		this.univer = univer;
		this.primaryPersona = primaryPersona;
		this.primaryProfesion = primaryProfesion;
	}
}
