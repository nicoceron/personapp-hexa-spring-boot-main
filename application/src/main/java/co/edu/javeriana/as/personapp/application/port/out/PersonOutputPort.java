package co.edu.javeriana.as.personapp.application.port.out;

import java.util.List;

import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.domain.Person;

@Port
public interface PersonOutputPort {
	public Person save(Person person);
	public Boolean delete(Long cc);
	public List<Person> findAll();
	public Person findById(Long cc);
}
