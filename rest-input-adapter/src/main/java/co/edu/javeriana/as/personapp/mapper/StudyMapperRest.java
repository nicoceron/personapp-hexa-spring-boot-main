package co.edu.javeriana.as.personapp.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.model.request.StudyRequest;
import co.edu.javeriana.as.personapp.model.response.StudyResponse;

@Mapper(componentModel = "spring")
public interface StudyMapperRest {

    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; // YYYY-MM-DD

    @Mapping(target = "person", source = "personId", qualifiedByName = "stringToPerson")
    @Mapping(target = "profession", source = "professionId", qualifiedByName = "stringToProfession")
    @Mapping(target = "graduationDate", source = "graduationDate", qualifiedByName = "stringToLocalDate")
    Study fromRequestToDomain(StudyRequest request);

    @Mapping(target = "personId", source = "study.person.identification")
    @Mapping(target = "professionId", source = "study.profession.identification")
    @Mapping(target = "graduationDate", source = "study.graduationDate", qualifiedByName = "localDateToString")
    StudyResponse fromDomainToResponse(Study study, String database, String status);

    @Named("stringToPerson")
    default Person stringToPerson(String personId) {
        if (personId == null) return null;
        Person person = new Person();
        person.setIdentification(Integer.parseInt(personId));
        return person;
    }

    @Named("stringToProfession")
    default Profession stringToProfession(String professionId) {
        if (professionId == null) return null;
        Profession profession = new Profession();
        profession.setIdentification(Integer.parseInt(professionId));
        return profession;
    }

    @Named("stringToLocalDate")
    default LocalDate stringToLocalDate(String date) {
        return date == null ? null : LocalDate.parse(date, DATE_FORMATTER);
    }

    @Named("localDateToString")
    default String localDateToString(LocalDate date) {
        return date == null ? null : date.format(DATE_FORMATTER);
    }

} 