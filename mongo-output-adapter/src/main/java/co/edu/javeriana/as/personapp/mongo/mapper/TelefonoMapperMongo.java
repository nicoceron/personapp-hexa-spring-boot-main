package co.edu.javeriana.as.personapp.mongo.mapper;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument;
import lombok.NonNull;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public class TelefonoMapperMongo {

	@Autowired
	private PersonaMapperMongo personaMapperMongo;

	public TelefonoDocument fromDomainToAdapter(Phone phone) {
		if (phone == null) {
			return null;
		}
		Integer personId = null;
		if (phone.getOwner() != null && phone.getOwner().getIdentification() != null) {
			// Just store the person's ID instead of the entire PersonaDocument
			personId = phone.getOwner().getIdentification();
		}
		return new TelefonoDocument(
				phone.getNumber(),
				phone.getCompany(),
				personId
		);
	}

	public Phone fromAdapterToDomain(TelefonoDocument telefonoDocument) {
		if (telefonoDocument == null) {
			return null;
		}
		Person person = null;
		if (telefonoDocument.getDuenio() != null) {
			// Create a minimal Person with just the ID
			person = new Person();
			person.setIdentification(telefonoDocument.getDuenio());
		}
		return new Phone(
				telefonoDocument.getNum(),
				telefonoDocument.getOper(),
				person
		);
	}

	public List<Phone> fromAdapterListToDomainList(List<TelefonoDocument> telefonoDocuments) {
		if (telefonoDocuments == null) {
			return null;
		}
		return telefonoDocuments.stream()
				.map(this::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	public List<TelefonoDocument> fromDomainListToAdapterList(List<Phone> phones) {
		if (phones == null) {
			return null;
		}
		return phones.stream()
				.map(this::fromDomainToAdapter)
				.collect(Collectors.toList());
	}
}