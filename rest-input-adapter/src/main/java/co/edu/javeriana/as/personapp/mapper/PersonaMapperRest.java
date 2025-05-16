package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mapper
public class PersonaMapperRest {
	
	public PersonaResponse fromDomainToAdapterRestMaria(Person person) {
		return fromDomainToAdapterRest(person, "MARIA");
	}
	
	public PersonaResponse fromDomainToAdapterRestMongo(Person person) {
		return fromDomainToAdapterRest(person, "MONGO");
	}
	
	public PersonaResponse fromDomainToAdapterRest(Person person, String database) {
		return fromDomainToAdapterRest(person, database, "OK");
	}
	
	public PersonaResponse fromDomainToAdapterRest(Person person, String database, String status) {
		return new PersonaResponse(
				person.getIdentification()+"", 
				person.getFirstName(), 
				person.getLastName(), 
				person.getAge()+"", 
				person.getGender().toString(), 
				database,
				status);
	}

	public Person fromAdapterToDomain(PersonaRequest request) {
		Person person = new Person();
		person.setIdentification(Integer.parseInt(request.getDni()));
		person.setFirstName(request.getFirstName());
		person.setLastName(request.getLastName());
		person.setAge(request.getAge() != null && !request.getAge().isEmpty() ? Integer.parseInt(request.getAge()) : null );
		String genderStr = request.getSex();
		log.info("PersonaMapperRest: Converting gender string '{} ' to Gender enum.", genderStr);
		if (genderStr != null) {
			if (genderStr.equalsIgnoreCase("MASCULINO") || genderStr.equalsIgnoreCase("MALE") || genderStr.equalsIgnoreCase("M")) {
				person.setGender(Gender.MALE);
			} else if (genderStr.equalsIgnoreCase("FEMENINO") || genderStr.equalsIgnoreCase("FEMALE") || genderStr.equalsIgnoreCase("F")) {
				person.setGender(Gender.FEMALE);
			} else {
				person.setGender(Gender.OTHER);
			}
		} else {
		    log.warn("PersonaMapperRest: Gender string from request is null. Setting gender to OTHER by default.");
			person.setGender(Gender.OTHER);
		}
		log.info("PersonaMapperRest: Gender enum set to {} for DNI {}.", person.getGender(), person.getIdentification());
		return person;
	}
	
	public PersonaResponse createErrorResponse(String errorMessage, String database) {
		return new PersonaResponse(
				"", 
				"", 
				"", 
				"", 
				"", 
				database,
				"ERROR: " + errorMessage);
	}
}
