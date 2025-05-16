package co.edu.javeriana.as.personapp.mariadb.mapper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

		EstudiosEntity estudiosEntity = new EstudiosEntity(estudioPK);
		estudiosEntity.setFecha(study.getGraduationDate() != null ? Date.from(study.getGraduationDate().atStartOfDay(ZoneId.systemDefault()).toInstant()) : null);
		estudiosEntity.setUniver(study.getUniversityName());
		
		// Set related entities for FK relationship
		PersonaEntity personaEntity = new PersonaEntity();
		personaEntity.setCc(study.getPerson().getIdentification());
		// Potentially fetch the full PersonaEntity if needed by ORM, but for FK only ID is usually enough
		// personaEntity = personaRepositoryMaria.findById(study.getPerson().getIdentification()).orElse(null);
		estudiosEntity.setPersona(personaEntity);

		ProfesionEntity profesionEntity = new ProfesionEntity();
		profesionEntity.setId(study.getProfession().getIdentification());
		// Potentially fetch the full ProfesionEntity if needed
		// profesionEntity = profesionRepositoryMaria.findById(study.getProfession().getIdentification()).orElse(null);
		estudiosEntity.setProfesion(profesionEntity);
		
		return estudiosEntity;
	}

	public Study fromAdapterToDomain(EstudiosEntity estudiosEntity) {
		Person person = personaMapperMaria.fromAdapterToDomain(estudiosEntity.getPersona());
		Profession profession = profesionMapperMaria.fromAdapterToDomain(estudiosEntity.getProfesion());
		LocalDate graduationDate = estudiosEntity.getFecha() != null ? new java.sql.Date(estudiosEntity.getFecha().getTime()).toLocalDate() : null;
		return new Study(person, profession, graduationDate, estudiosEntity.getUniver());
	}

	public List<Study> fromAdapterListToDomainList(List<EstudiosEntity> estudiosEntities) {
		if (estudiosEntities == null) {
			return new ArrayList<>();
		}
		return estudiosEntities.stream()
				.map(this::fromAdapterToDomain)
				.collect(Collectors.toList());
	}
}