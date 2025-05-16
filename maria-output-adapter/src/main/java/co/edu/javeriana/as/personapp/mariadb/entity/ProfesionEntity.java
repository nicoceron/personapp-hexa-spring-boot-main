package co.edu.javeriana.as.personapp.mariadb.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Lob;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author aasanchez
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "profesion", schema = "persona_db")
public class ProfesionEntity {

	@Id
	@Column(name = "id", nullable = false, unique = true)
	private Integer id;

	@Column(name = "nom", nullable = false, length = 90)
	private String nombre;

	@Lob // For potentially long descriptions
	@Column(name = "des", nullable = true) // Description can be optional
	private String descripcion;
}
