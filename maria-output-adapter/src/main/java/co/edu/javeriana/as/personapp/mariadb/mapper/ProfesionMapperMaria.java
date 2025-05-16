package co.edu.javeriana.as.personapp.mariadb.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
// import org.mapstruct.Mappings; // Not needed for this simple case

import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.mariadb.entity.ProfesionEntity;

@Mapper(componentModel = "spring") // This will now be correctly interpreted
public interface ProfesionMapperMaria {

	// No need to map studies if ProfesionEntity doesn't have it directly
	// and Profession domain object's studies list is handled elsewhere or not persisted directly with profession
	@Mapping(source = "identification", target = "id")
	@Mapping(source = "name", target = "nombre")
	@Mapping(source = "description", target = "descripcion")
	ProfesionEntity fromDomainToAdapter(Profession profession);

	@Mapping(source = "id", target = "identification")
	@Mapping(source = "nombre", target = "name")
	@Mapping(source = "descripcion", target = "description")
	Profession fromAdapterToDomain(ProfesionEntity profesionEntity);
}
