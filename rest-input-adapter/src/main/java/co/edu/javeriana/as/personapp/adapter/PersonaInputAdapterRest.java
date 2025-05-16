package co.edu.javeriana.as.personapp.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mapper.PersonaMapperRest;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import co.edu.javeriana.as.personapp.model.response.PhoneResponse;
import co.edu.javeriana.as.personapp.model.response.StudyResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class PersonaInputAdapterRest {

	@Autowired
	@Qualifier("personOutputAdapterMaria")
	private PersonOutputPort personOutputPortMaria;

	@Autowired
	@Qualifier("personOutputAdapterMongo")
	private PersonOutputPort personOutputPortMongo;

	@Autowired
	private PersonInputPort personInputPort;

	@Autowired
	private PersonaMapperRest personaMapperRest;

	private String setPersonOutputPortInjection(String dbOption) throws InvalidOptionException {
		if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
			personInputPort.setPersistence(personOutputPortMaria);
			return DatabaseOption.MARIA.toString();
		} else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
			personInputPort.setPersistence(personOutputPortMongo);
			return DatabaseOption.MONGO.toString();
		} else {
			throw new InvalidOptionException("Invalid database option: " + dbOption);
		}
	}

	public List<PersonaResponse> findAll(String database) {
		log.info("Into findAll PersonaEntity in Input Adapter");
		try {
			String dbType = setPersonOutputPortInjection(database);
			if (dbType.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
				return personInputPort.findAll().stream().map(personaMapperRest::fromDomainToAdapterRestMaria)
						.collect(Collectors.toList());
			} else {
				return personInputPort.findAll().stream().map(personaMapperRest::fromDomainToAdapterRestMongo)
						.collect(Collectors.toList());
			}
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			return new ArrayList<PersonaResponse>();
		}
	}

	public PersonaResponse create(PersonaRequest request) {
		log.info("Into create PersonaEntity in Input Adapter, request DNI: {}", request.getDni());
		try {
			setPersonOutputPortInjection(request.getDatabase());
			log.info("Mapping PersonaRequest to Person domain object. Gender from request: {}", request.getSex());
			Person person = personaMapperRest.fromAdapterToDomain(request);
			log.info("Person domain object created. Gender set to: {}. Identification: {}", person.getGender(), person.getIdentification());
			if (person.getGender() == null) {
			    log.error("CRITICAL: Person domain object has NULL gender before calling personInputPort.create!");
            }
			Person createdPerson = personInputPort.create(person);
			log.info("Person domain object returned from personInputPort.create. Gender: {}", createdPerson.getGender());
            if (createdPerson.getGender() == null) {
                log.error("CRITICAL: Person domain object has NULL gender after personInputPort.create!");
            }
			return personaMapperRest.fromDomainToAdapterRest(createdPerson, request.getDatabase());
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			return new PersonaResponse(request.getDni(), request.getFirstName(), request.getLastName(), 
					request.getAge(), request.getSex(), request.getDatabase(), "ERROR: " + e.getMessage());
		} catch (Exception e) {
			log.error("Error creating person in PersonaInputAdapterRest: " + e.getMessage(), e);
			return new PersonaResponse(request.getDni(), request.getFirstName(), request.getLastName(),
					request.getAge(), request.getSex(), request.getDatabase(), "ERROR: " + e.getMessage());
		}
	}
	
	public PersonaResponse edit(Long identification, PersonaRequest request) {
		log.info("Into edit PersonaEntity in Input Adapter");
		try {
			setPersonOutputPortInjection(request.getDatabase());
			Person person = personaMapperRest.fromAdapterToDomain(request);
			person = personInputPort.edit(identification, person);
			return personaMapperRest.fromDomainToAdapterRest(person, request.getDatabase());
		} catch (InvalidOptionException | NoExistException e) {
			log.warn(e.getMessage());
			return new PersonaResponse(request.getDni(), request.getFirstName(), request.getLastName(), 
					request.getAge(), request.getSex(), request.getDatabase(), "ERROR: " + e.getMessage());
		}
	}
	
	public PersonaResponse delete(Long identification, String database) {
		log.info("Into delete PersonaEntity in Input Adapter");
		try {
			setPersonOutputPortInjection(database);
			Person person = personInputPort.findOne(identification); // Needed to return details in response
			Boolean result = personInputPort.drop(identification);
			if (result) {
				return personaMapperRest.fromDomainToAdapterRest(person, database, "DELETED");
			} else {
				// This case might not be reached if drop throws NoExistException
				return personaMapperRest.createErrorResponse("Failed to delete person with ID: " + identification, database);
			}
		} catch (InvalidOptionException | NoExistException e) {
			log.warn(e.getMessage());
			return personaMapperRest.createErrorResponse(e.getMessage(), database);
		}
	}
	
	public PersonaResponse findById(Long identification, String database) {
		log.info("Into findById PersonaEntity in Input Adapter");
		try {
			setPersonOutputPortInjection(database);
			Person person = personInputPort.findOne(identification);
			return personaMapperRest.fromDomainToAdapterRest(person, database);
		} catch (InvalidOptionException | NoExistException e) {
			log.warn(e.getMessage());
			return personaMapperRest.createErrorResponse(e.getMessage(), database);
		}
	}
	
	public Integer count(String database) {
		log.info("Into count PersonaEntity in Input Adapter");
		try {
			setPersonOutputPortInjection(database);
			return personInputPort.count();
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			return 0;
		}
	}
	
	public List<PhoneResponse> getPhones(Long identification, String database) {
		log.info("Into getPhones PersonaEntity in Input Adapter");
		try {
			setPersonOutputPortInjection(database);
			List<Phone> phones = personInputPort.getPhones(identification);
			return phones.stream()
					.map(phone -> new PhoneResponse(
							phone.getNumber(),
							phone.getCompany(), 
							identification.toString(), 
							database, 
							"OK"))
					.collect(Collectors.toList());
		} catch (InvalidOptionException | NoExistException e) {
			log.warn(e.getMessage());
			return new ArrayList<>();
		}
	}
	
	public List<StudyResponse> getStudies(Long identification, String database) {
		log.info("Into getStudies PersonaEntity in Input Adapter");
		try {
			setPersonOutputPortInjection(database);
			List<Study> studies = personInputPort.getStudies(identification);
			return studies.stream()
					.map(study -> new StudyResponse(
							identification.toString(),
							study.getProfession().getIdentification().toString(),
							study.getGraduationDate() != null ? study.getGraduationDate().toString() : "",
							study.getUniversityName(),
							database,
							"OK"))
					.collect(Collectors.toList());
		} catch (InvalidOptionException | NoExistException e) {
			log.warn(e.getMessage());
			return new ArrayList<>();
		}
	}
}
