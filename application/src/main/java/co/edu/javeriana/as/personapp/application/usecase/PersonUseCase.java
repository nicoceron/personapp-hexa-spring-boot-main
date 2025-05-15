package co.edu.javeriana.as.personapp.application.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Study;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
public class PersonUseCase implements PersonInputPort {

	private PersonOutputPort personPersistance;
	
	public PersonUseCase(@Qualifier("personOutputAdapterMaria") PersonOutputPort personOutputPort) {
		this.personPersistance = personOutputPort;
	}
	
	@Override
	public void setPersistence(PersonOutputPort personOutputPort) {
		this.personPersistance = personOutputPort;
	}

	@Override
	public Person create(Person person) {
		log.debug("Into create Person UseCase");
		return personPersistance.save(person);
	}

	@Override
	public Person edit(Long cc, Person person) throws NoExistException {
		log.debug("Into edit Person UseCase");
		Person oldPerson = personPersistance.findById(cc);
		if (oldPerson != null)
			return personPersistance.save(person);
		throw new NoExistException(
				"The person with id " + cc + " does not exist into db, cannot be edited");
	}

	@Override
	public Boolean drop(Long cc) throws NoExistException {
		log.debug("Into drop Person UseCase");
		Person oldPerson = personPersistance.findById(cc);
		if (oldPerson != null)
			return personPersistance.delete(cc);
		throw new NoExistException(
				"The person with id " + cc + " does not exist into db, cannot be dropped");
	}

	@Override
	public List<Person> findAll() {
		log.debug("Into findAll Person UseCase");
		return personPersistance.findAll();
	}

	@Override
	public Person findOne(Long cc) throws NoExistException {
		log.debug("Into findOne Person UseCase");
		Person oldPerson = personPersistance.findById(cc);
		if (oldPerson != null)
			return oldPerson;
		throw new NoExistException(
				"The person with id " + cc + " does not exist into db, cannot be found");
	}

	@Override
	public Integer count() {
		return findAll().size();
	}

	@Override
	public List<Phone> getPhones(Long cc) throws NoExistException {
		Person oldPerson = personPersistance.findById(cc);
		if (oldPerson != null)
			return oldPerson.getPhoneNumbers();
		throw new NoExistException(
				"The person with id " + cc + " does not exist into db, cannot get phones");
	}

	@Override
	public List<Study> getStudies(Long cc) throws NoExistException {
		Person oldPerson = personPersistance.findById(cc);
		if (oldPerson != null)
			return oldPerson.getStudies();
		throw new NoExistException(
				"The person with id " + cc + " does not exist into db, cannot get studies");
	}
}
