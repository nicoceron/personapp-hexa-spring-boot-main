package co.edu.javeriana.as.personapp.mongo.adapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.MongoWriteException;

import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.mapper.PersonaMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.PersonaRepositoryMongo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter("personOutputAdapterMongo")
public class PersonOutputAdapterMongo implements PersonOutputPort {
	
	@Autowired
    private PersonaRepositoryMongo personaRepositoryMongo;
	
	@Autowired
	private PersonaMapperMongo personaMapperMongo;
	
	@Override
	public Person save(Person person) {
		log.debug("Into save PersonEntity in MongoDB Adapter");
		try {
			PersonaDocument persistedPersona = personaRepositoryMongo.save(personaMapperMongo.fromDomainToAdapter(person));
			return personaMapperMongo.fromAdapterToDomain(persistedPersona);
		} catch (MongoWriteException e) {
			log.error("Error saving person to MongoDB", e);
			// Depending on the desired behavior, you might re-throw, return null, or a specific error object.
			return null;
		}		
	}

	@Override
	public Boolean delete(Long cc) {
		log.debug("Into delete PersonEntity in MongoDB Adapter");
		personaRepositoryMongo.deleteById(cc.intValue());
		return !personaRepositoryMongo.existsById(cc.intValue());
	}

	@Override
	public List<Person> findAll() {
		log.debug("Into findAll PersonEntity in MongoDB Adapter");
		return personaRepositoryMongo.findAll().stream()
				.map(personaMapperMongo::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	@Override
	public Person findById(Long cc) {
		log.debug("Into findById PersonEntity in MongoDB Adapter");
		Optional<PersonaDocument> optionalPersonaDocument = personaRepositoryMongo.findById(cc.intValue());
		return optionalPersonaDocument.map(personaMapperMongo::fromAdapterToDomain).orElse(null);
	}

}
