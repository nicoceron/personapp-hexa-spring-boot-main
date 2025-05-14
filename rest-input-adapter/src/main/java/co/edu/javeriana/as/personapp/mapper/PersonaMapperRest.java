package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;

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
		if (request.getAge() != null && !request.getAge().isEmpty()) {
			person.setAge(Integer.parseInt(request.getAge()));
		}
		if ("M".equalsIgnoreCase(request.getSex())) {
			person.setGender(Gender.MALE);
		} else if ("F".equalsIgnoreCase(request.getSex())) {
			person.setGender(Gender.FEMALE);
		}
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
