package co.edu.javeriana.as.personapp.mongo.mapper;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument;
import lombok.NonNull;

@Mapper
public class EstudiosMapperMongo {

	@Autowired
	private PersonaMapperMongo personaMapperMongo;

	@Autowired
	private ProfesionMapperMongo profesionMapperMongo;

	public EstudiosDocument fromDomainToAdapter(Study study) {
		EstudiosDocument estudio = new EstudiosDocument();
		estudio.setId(validateId(study.getPerson().getIdentification(), study.getProfession().getIdentification()));
		estudio.setPrimaryPersona(validatePrimaryPersona(study.getPerson()));
		estudio.setPrimaryProfesion(validatePrimaryProfesion(study.getProfession()));
		estudio.setFecha(validateFecha(study.getGraduationDate()));
		estudio.setUniver(validateUniver(study.getUniversityName()));
		return estudio;
	}

	private String validateId(@NonNull Integer identificationPerson, @NonNull Integer identificationProfession) {
		return identificationPerson + "-" + identificationProfession;
	}

	private PersonaDocument validatePrimaryPersona(@NonNull Person person) {
		if (person == null) return new PersonaDocument();
		
		// Just create a simple document with basic data to avoid circular dependencies
		PersonaDocument personaDocument = new PersonaDocument();
		personaDocument.setId(person.getIdentification());
		personaDocument.setNombre(person.getFirstName());
		personaDocument.setApellido(person.getLastName());
		personaDocument.setGenero(person.getGender() == Gender.FEMALE ? "F" : person.getGender() == Gender.MALE ? "M" : " ");
		personaDocument.setEdad(person.getAge());
		return personaDocument;
	}

	private ProfesionDocument validatePrimaryProfesion(@NonNull Profession profession) {
		if (profession == null) return new ProfesionDocument();
		
		// Just create a simple document with basic data to avoid circular dependencies
		ProfesionDocument profesionDocument = new ProfesionDocument();
		profesionDocument.setId(profession.getIdentification());
		profesionDocument.setNom(profession.getName());
		profesionDocument.setDes(profession.getDescription());
		return profesionDocument;
	}

	private LocalDate validateFecha(LocalDate graduationDate) {
		return graduationDate != null ? graduationDate : null;
	}

	private String validateUniver(String universityName) {
		return universityName != null ? universityName : "";
	}

	public Study fromAdapterToDomain(EstudiosDocument estudiosDocument) {
		Study study = new Study();
		
		// Create a simplified Person to avoid circular dependencies
		Person person = new Person();
		PersonaDocument personaDocument = estudiosDocument.getPrimaryPersona();
		if (personaDocument != null) {
			person.setIdentification(personaDocument.getId());
			person.setFirstName(personaDocument.getNombre());
			person.setLastName(personaDocument.getApellido());
			person.setGender("F".equals(personaDocument.getGenero()) ? Gender.FEMALE : 
				"M".equals(personaDocument.getGenero()) ? Gender.MALE : Gender.OTHER);
			person.setAge(personaDocument.getEdad());
		}
		study.setPerson(person);
		
		// Create a simplified Profession to avoid circular dependencies
		Profession profession = new Profession();
		ProfesionDocument profesionDocument = estudiosDocument.getPrimaryProfesion();
		if (profesionDocument != null) {
			profession.setIdentification(profesionDocument.getId());
			profession.setName(profesionDocument.getNom());
			profession.setDescription(profesionDocument.getDes());
		}
		study.setProfession(profession);
		
		study.setGraduationDate(validateGraduationDate(estudiosDocument.getFecha()));
		study.setUniversityName(validateUniversityName(estudiosDocument.getUniver()));
		return study;
	}

	private LocalDate validateGraduationDate(LocalDate fecha) {
		return fecha != null ? fecha : null;
	}

	private String validateUniversityName(String univer) {
		return univer != null ? univer : "";
	}
}