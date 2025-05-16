package co.edu.javeriana.as.personapp.mongo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument;

@Mapper(componentModel = "spring")
public interface ProfesionMapperMongo {

	@Mapping(source = "identification", target = "id")
	@Mapping(source = "name", target = "nombre")
	@Mapping(source = "description", target = "descripcion")
	ProfesionDocument fromDomainToAdapter(Profession profession);

	@Mapping(source = "id", target = "identification")
	@Mapping(source = "nombre", target = "name")
	@Mapping(source = "descripcion", target = "description")
	Profession fromAdapterToDomain(ProfesionDocument profesionDocument);
}
