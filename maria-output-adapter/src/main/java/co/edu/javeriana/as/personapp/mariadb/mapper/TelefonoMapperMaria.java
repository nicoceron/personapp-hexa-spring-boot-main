package co.edu.javeriana.as.personapp.mariadb.mapper;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.TelefonoEntity;
import lombok.NonNull;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public class TelefonoMapperMaria {

	@Autowired
	private PersonaMapperMaria personaMapperMaria;

	public TelefonoEntity fromDomainToAdapter(Phone phone) {
		if (phone == null) {
			return null;
		}
		PersonaEntity personaEntity = null;
		if (phone.getOwner() != null) {
			// Avoid full mapping if only ID is needed or to prevent cycles
			personaEntity = new PersonaEntity();
			personaEntity.setCc(phone.getOwner().getIdentification());
		}
		return new TelefonoEntity(
				phone.getNumber(),
				phone.getCompany(),
				personaEntity
		);
	}

	private PersonaEntity validateDuenio(@NonNull Person owner) {
		if (owner == null) return new PersonaEntity();
		
		// Just create a simple entity with basic data to avoid circular dependencies
		PersonaEntity personaEntity = new PersonaEntity();
		personaEntity.setCc(owner.getIdentification());
		personaEntity.setNombre(owner.getFirstName());
		personaEntity.setApellido(owner.getLastName());
		personaEntity.setGenero(owner.getGender() == Gender.FEMALE ? 'F' : owner.getGender() == Gender.MALE ? 'M' : ' ');
		personaEntity.setEdad(owner.getAge());
		return personaEntity;
	}

	public Phone fromAdapterToDomain(TelefonoEntity telefonoEntity) {
		if (telefonoEntity == null) {
			return null;
		}
		Person person = null;
		if (telefonoEntity.getDuenio() != null) {
			person = personaMapperMaria.fromAdapterToDomain(telefonoEntity.getDuenio());
		}
		return new Phone(
				telefonoEntity.getNum(),
				telefonoEntity.getOperador(),
				person
		);
	}

	public List<Phone> fromAdapterListToDomainList(List<TelefonoEntity> telefonoEntities) {
		if (telefonoEntities == null) {
			return null;
		}
		return telefonoEntities.stream()
				.map(this::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	public List<TelefonoEntity> fromDomainListToAdapterList(List<Phone> phones) {
		if (phones == null) {
			return null;
		}
		return phones.stream()
				.map(this::fromDomainToAdapter)
				.collect(Collectors.toList());
	}
}