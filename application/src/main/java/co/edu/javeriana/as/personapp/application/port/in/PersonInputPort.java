package co.edu.javeriana.as.personapp.application.port.in;

import java.util.List;

import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Study;

@Port
public interface PersonInputPort {
	
	void setPersistence(PersonOutputPort personOutputPort);
	
	Person create(Person person);

	Person edit(Long cc, Person person) throws NoExistException;

	Boolean drop(Long cc) throws NoExistException;

	List<Person> findAll();

	Person findOne(Long cc) throws NoExistException;

	Integer count();

	List<Phone> getPhones(Long cc) throws NoExistException;

	List<Study> getStudies(Long cc) throws NoExistException;
}
