package co.edu.javeriana.as.personapp.mariadb.mapper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntityPK;
import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.ProfesionEntity;

@Mapper
public class EstudiosMapperMaria {

	@Autowired
	private PersonaMapperMaria personaMapperMaria;

	@Autowired
	private ProfesionMapperMaria profesionMapperMaria;

	public EstudiosEntity fromDomainToAdapter(Study study) {
		EstudiosEntityPK estudioPK = new EstudiosEntityPK();
		estudioPK.setCcPer(study.getPerson().getIdentification());
		estudioPK.setIdProf(study.getProfession().getIdentification());
		EstudiosEntity estudio = new EstudiosEntity();
		estudio.setEstudiosPK(estudioPK);
		estudio.setFecha(validateFecha(study.getGraduationDate()));
		estudio.setUniver(validateUniver(study.getUniversityName()));
		return estudio;
	}

	private Date validateFecha(LocalDate graduationDate) {
		return graduationDate != null
				? Date.from(graduationDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
				: null;
	}

	private String validateUniver(String universityName) {
		return universityName != null ? universityName : "";
	}

	public Study fromAdapterToDomain(EstudiosEntity estudiosEntity) {
		Study study = new Study();
		
		// Create a simplified Person to avoid circular dependencies
		Person person = new Person();
		PersonaEntity personaEntity = estudiosEntity.getPersona();
		if (personaEntity != null) {
			person.setIdentification(personaEntity.getCc());
			person.setFirstName(personaEntity.getNombre());
			person.setLastName(personaEntity.getApellido());
			person.setGender(personaEntity.getGenero() == 'F' ? Gender.FEMALE : 
				personaEntity.getGenero() == 'M' ? Gender.MALE : Gender.OTHER);
			person.setAge(personaEntity.getEdad());
		}
		study.setPerson(person);
		
		// Create a simplified Profession to avoid circular dependencies
		Profession profession = new Profession();
		ProfesionEntity profesionEntity = estudiosEntity.getProfesion();
		if (profesionEntity != null) {
			profession.setIdentification(profesionEntity.getId());
			profession.setName(profesionEntity.getNom());
			profession.setDescription(profesionEntity.getDes());
		}
		study.setProfession(profession);
		
		study.setGraduationDate(validateGraduationDate(estudiosEntity.getFecha()));
		study.setUniversityName(validateUniversityName(estudiosEntity.getUniver()));
		return study;
	}

	private LocalDate validateGraduationDate(Date fecha) {
		return fecha != null ? fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
	}

	private String validateUniversityName(String univer) {
		return univer != null ? univer : "";
	}
}