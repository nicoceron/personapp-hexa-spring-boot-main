package co.edu.javeriana.as.personapp.mariadb.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.TelefonoEntity;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mapper
public class PersonaMapperMaria {

	@Autowired
	private EstudiosMapperMaria estudiosMapperMaria;

	@Autowired
	private TelefonoMapperMaria telefonoMapperMaria;

	public PersonaEntity fromDomainToAdapter(Person person) {
		PersonaEntity personaEntity = new PersonaEntity();
		log.info("PersonaMapperMaria: Mapping Person domain (ID: {}) to PersonaEntity. Gender from domain: {}", person.getIdentification(), person.getGender());
		if (person.getGender() == null) {
		    log.error("CRITICAL: Person domain object has NULL gender when trying to map to PersonaEntity!");
        }
		personaEntity.setCc(person.getIdentification());
		personaEntity.setNombre(person.getFirstName());
		personaEntity.setApellido(person.getLastName());
		personaEntity.setGenero(validateGenero(person.getGender()));
		personaEntity.setEdad(validateEdad(person.getAge()));
		personaEntity.setEstudios(validateEstudios(person.getStudies()));
		personaEntity.setTelefonos(validateTelefonos(person.getPhoneNumbers()));
		return personaEntity;
	}

	private Character validateGenero(@NonNull Gender gender) {
		return gender == Gender.FEMALE ? 'F' : gender == Gender.MALE ? 'M' : ' ';
	}

	private Integer validateEdad(Integer age) {
		return age != null && age >= 0 ? age : null;
	}

	private List<EstudiosEntity> validateEstudios(List<Study> studies) {
		return studies != null && !studies.isEmpty()
				? studies.stream().map(study -> estudiosMapperMaria.fromDomainToAdapter(study)).collect(Collectors.toList())
				: new ArrayList<EstudiosEntity>();
	}

	private List<TelefonoEntity> validateTelefonos(List<Phone> phoneNumbers) {
		return phoneNumbers != null && !phoneNumbers.isEmpty() ? phoneNumbers.stream()
				.map(phone -> telefonoMapperMaria.fromDomainToAdapter(phone)).collect(Collectors.toList())
				: new ArrayList<TelefonoEntity>();
	}

	public Person fromAdapterToDomain(PersonaEntity personaEntity) {
		if (personaEntity == null) {
			return null; // Or handle as an empty Person domain object if preferred
		}
		Person person = new Person();
		person.setIdentification(personaEntity.getCc());
		person.setFirstName(personaEntity.getNombre());
		person.setLastName(personaEntity.getApellido());
		person.setGender(validateGender(personaEntity.getGenero()));
		person.setAge(validateAge(personaEntity.getEdad()));
		
		// Skip study relationships to avoid circular dependencies
		person.setStudies(new ArrayList<Study>());
		if (personaEntity.getTelefonos() != null) {
			person.setPhoneNumbers(personaEntity.getTelefonos().stream()
				.map(telefonoEntity -> {
					// Create Phone without setting its owner to break the cycle
					Phone phone = new Phone();
					phone.setNumber(telefonoEntity.getNum());
					phone.setCompany(telefonoEntity.getOperador());
					// phone.setOwner(person); // This would cause the cycle
					return phone;
				})
				.collect(Collectors.toList()));
		} else {
			person.setPhoneNumbers(new ArrayList<Phone>());
		}
		return person;
	}

	private @NonNull Gender validateGender(Character genero) {
		return genero == 'F' ? Gender.FEMALE : genero == 'M' ? Gender.MALE : Gender.OTHER;
	}

	private Integer validateAge(Integer edad) {
		return edad != null && edad >= 0 ? edad : null;
	}

	// This method is intentionally not used to avoid circular dependencies
	private List<Study> validateStudiesOld(List<EstudiosEntity> estudiosEntity) {
		return estudiosEntity != null && !estudiosEntity.isEmpty() ? estudiosEntity.stream()
				.map(estudio -> estudiosMapperMaria.fromAdapterToDomain(estudio)).collect(Collectors.toList())
				: new ArrayList<Study>();
	}

	private List<Phone> validatePhones(List<TelefonoEntity> telefonoEntities) {
		return telefonoEntities != null && !telefonoEntities.isEmpty() ? telefonoEntities.stream()
				.map(telefono -> telefonoMapperMaria.fromAdapterToDomain(telefono)).collect(Collectors.toList())
				: new ArrayList<Phone>();
	}
}