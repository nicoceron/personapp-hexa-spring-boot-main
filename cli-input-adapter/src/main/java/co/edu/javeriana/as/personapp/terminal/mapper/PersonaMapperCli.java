package co.edu.javeriana.as.personapp.terminal.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.terminal.model.PersonaModelCli;

@Mapper
public class PersonaMapperCli {

	public PersonaModelCli fromDomainToAdapterCli(Person person) {
		PersonaModelCli personaModelCli = new PersonaModelCli();
		personaModelCli.setCc(person.getIdentification());
		personaModelCli.setNombre(person.getFirstName());
		personaModelCli.setApellido(person.getLastName());
		personaModelCli.setGenero(person.getGender().toString());
		personaModelCli.setEdad(person.getAge());
		return personaModelCli;
	}

	public Person fromAdapterToDomain(PersonaModelCli personaModelCli) {
		Person person = new Person();
		person.setIdentification(personaModelCli.getCc());
		person.setFirstName(personaModelCli.getNombre());
		person.setLastName(personaModelCli.getApellido());
		person.setAge(personaModelCli.getEdad());
		// Gender conversion
		String genderStr = personaModelCli.getGenero();
		if (genderStr != null) {
			if (genderStr.equalsIgnoreCase("MALE") || genderStr.equalsIgnoreCase("MASCULINO")) {
				person.setGender(Gender.MALE);
			} else if (genderStr.equalsIgnoreCase("FEMALE") || genderStr.equalsIgnoreCase("FEMENINO")) {
				person.setGender(Gender.FEMALE);
			} else {
				person.setGender(Gender.OTHER);
			}
		} else {
			person.setGender(Gender.OTHER); // Default or handle as an error
		}
		return person;
	}
}
