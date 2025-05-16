package co.edu.javeriana.as.personapp.mongo.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument;
import lombok.NonNull;

@Mapper
public class PersonaMapperMongo {

	private static final Logger log = LoggerFactory.getLogger(PersonaMapperMongo.class);

	@Autowired
	private EstudiosMapperMongo estudiosMapperMongo;

	@Autowired
	private TelefonoMapperMongo telefonoMapperMongo;

	public PersonaDocument fromDomainToAdapter(Person person) {
		PersonaDocument personaDocument = new PersonaDocument();
		personaDocument.setId(person.getIdentification());
		personaDocument.setNombre(person.getFirstName());
		personaDocument.setApellido(person.getLastName());
		personaDocument.setGenero(validateGenero(person.getGender()));
		personaDocument.setEdad(validateEdad(person.getAge()));
		// personaDocument.setEstudios(validateEstudios(person.getStudies())); // Field commented out in PersonaDocument
		// personaDocument.setTelefonos(validateTelefonos(person.getPhoneNumbers())); // Field commented out in PersonaDocument
		return personaDocument;
	}

	private String validateGenero(@NonNull Gender gender) {
		return gender == Gender.FEMALE ? "F" : gender == Gender.MALE ? "M" : " ";
	}

	private Integer validateEdad(Integer age) {
		return age != null && age >= 0 ? age : null;
	}

	private List<EstudiosDocument> validateEstudios(List<Study> studies) {
		return studies != null && !studies.isEmpty() ? studies.stream()
				.map(study -> estudiosMapperMongo.fromDomainToAdapter(study)).collect(Collectors.toList())
				: new ArrayList<EstudiosDocument>();
	}

	private List<TelefonoDocument> validateTelefonos(List<Phone> phoneNumbers) {
		return phoneNumbers != null && !phoneNumbers.isEmpty() ? phoneNumbers.stream()
				.map(phone -> telefonoMapperMongo.fromDomainToAdapter(phone)).collect(Collectors.toList())
				: new ArrayList<TelefonoDocument>();
	}

	public Person fromAdapterToDomain(PersonaDocument personaDocument) {
		log.info("Mapping PersonaDocument to Person domain: {}", personaDocument != null ? personaDocument.toString() : "null");
		if (personaDocument == null) {
			log.warn("PersonaDocument received in mapper is null.");
			return null; // Or throw, depending on desired behavior for null input
		}

		// Log individual fields if toString() isn't revealing enough due to Lombok or proxies
		if (personaDocument != null) {
			log.info("PersonaDocument ID: {}", personaDocument.getId());
			log.info("PersonaDocument Nombre: {}", personaDocument.getNombre());
			log.info("PersonaDocument Apellido: {}", personaDocument.getApellido());
			log.info("PersonaDocument Genero: {}", personaDocument.getGenero());
			log.info("PersonaDocument Edad: {}", personaDocument.getEdad());
		}

		Person person = new Person();
		person.setIdentification(personaDocument.getId()); // Expect this to be non-null
		
		// Allow potential NPEs here to see if fields are truly null
		person.setFirstName(personaDocument.getNombre());
		person.setLastName(personaDocument.getApellido());
		person.setGender(validateGender(personaDocument.getGenero())); // validateGender handles null input by returning OTHER
		person.setAge(validateAge(personaDocument.getEdad()));
		
		// Estudios and Telefonos are currently commented out in PersonaDocument, so their lists would be null/empty
		// and validateStudies/validatePhones should handle that by returning empty lists for Person domain.
		// However, since they are commented out in PersonaDocument, personaDocument.getEstudios() might not even exist.
		// Let's ensure these methods are robust to null inputs from PersonaDocument fields that might not exist.
		List<EstudiosDocument> estudiosDocs = null;
        List<TelefonoDocument> telefonosDocs = null;

        // try {
        //     // Attempt to get them if the methods exist, otherwise they remain null
        //     // This part is tricky as the fields are commented out in the class definition
        //     // So, direct calls like personaDocument.getEstudios() will fail compilation if fields are truly gone.
        //     // For now, assuming these lists will be effectively empty or handled if PersonaDocument is simplified.
        // } catch (Exception e) {
        //     log.warn("Could not access estudios/telefonos from PersonaDocument, possibly due to commented fields.");
        // }

		person.setStudies(validateStudies(estudiosDocs)); 
		person.setPhoneNumbers(validatePhones(telefonosDocs));
		
		log.info("Mapped to Person domain: {}", person.toString());
		return person;
	}

	private @NonNull Gender validateGender(String genero) {
	    if (genero == null) {
            log.warn("Genero string is null, defaulting to OTHER");
            return Gender.OTHER;
        }
		return "F".equals(genero) ? Gender.FEMALE : "M".equals(genero) ? Gender.MALE : Gender.OTHER;
	}

	private Integer validateAge(Integer edad) {
		return edad != null && edad >= 0 ? edad : null;
	}

	private List<Study> validateStudies(List<EstudiosDocument> estudiosDocuments) {
		return estudiosDocuments != null && !estudiosDocuments.isEmpty() ? estudiosDocuments.stream()
				.map(estudio -> estudiosMapperMongo.fromAdapterToDomain(estudio)).collect(Collectors.toList())
				: new ArrayList<Study>();
	}

	private List<Phone> validatePhones(List<TelefonoDocument> telefonosDocuments) {
		return telefonosDocuments != null && !telefonosDocuments.isEmpty() ? telefonosDocuments.stream()
				.map(telefono -> telefonoMapperMongo.fromAdapterToDomain(telefono)).collect(Collectors.toList())
				: new ArrayList<Phone>();
	}
}