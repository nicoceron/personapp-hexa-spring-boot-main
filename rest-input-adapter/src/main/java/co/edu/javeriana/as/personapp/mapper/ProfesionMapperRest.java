package co.edu.javeriana.as.personapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;

@Mapper(componentModel = "spring")
public interface ProfesionMapperRest {

    @Mapping(target = "studies", ignore = true) // Profession domain has studies, request does not directly.
    Profession fromRequestToDomain(ProfesionRequest request);

    // Removed redundant source attributes as they match target names
    ProfesionResponse fromDomainToResponse(Profession profession, String database, String status);

    // Helper to create a basic response from domain, status and database set separately.
    default ProfesionResponse fromDomainToResponse(Profession profession) {
        if (profession == null) {
            return null;
        }
        ProfesionResponse response = new ProfesionResponse();
        response.setIdentification(String.valueOf(profession.getIdentification()));
        response.setName(profession.getName());
        response.setDescription(profession.getDescription());
        // database and status will be set by the caller
        return response;
    }
} 