package co.edu.javeriana.as.personapp.mongo.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "profesion")
public class ProfesionDocument {

	@Id
	private Integer id; // Corresponds to Profession's identification
	private String nombre; // Corresponds to Profession's name
	private String descripcion; // Corresponds to Profession's description

	// No direct mapping for List<Study> studies from domain.Profession here
	// as it's not typically stored embedded this way unless specifically designed.
	// If studies were to be embedded, this class would need a List<StudyDocument> or similar.
}
