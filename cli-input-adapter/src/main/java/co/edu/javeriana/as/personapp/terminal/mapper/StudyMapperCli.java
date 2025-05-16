package co.edu.javeriana.as.personapp.terminal.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.terminal.model.StudyModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mapper
public class StudyMapperCli {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; // YYYY-MM-DD

    public StudyModelCli fromDomainToAdapterCli(Study study) {
        StudyModelCli studyModelCli = new StudyModelCli();
        studyModelCli.setPersonId(study.getPerson().getIdentification());
        studyModelCli.setProfessionId(study.getProfession().getIdentification());
        studyModelCli.setGraduationDate(study.getGraduationDate() != null ? study.getGraduationDate().format(DATE_FORMATTER) : null);
        studyModelCli.setUniversityName(study.getUniversityName());
        return studyModelCli;
    }

    public Study fromAdapterCliToDomain(StudyModelCli studyModelCli) {
        Person person = new Person();
        person.setIdentification(studyModelCli.getPersonId());

        Profession profession = new Profession();
        profession.setIdentification(studyModelCli.getProfessionId());

        LocalDate graduationDate = null;
        if (studyModelCli.getGraduationDate() != null && !studyModelCli.getGraduationDate().trim().isEmpty()) {
            try {
                graduationDate = LocalDate.parse(studyModelCli.getGraduationDate(), DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                log.warn("Invalid date format for graduation date: {}. Expected YYYY-MM-DD.", studyModelCli.getGraduationDate());
                // Optionally, rethrow or handle as an error
            }
        }

        return new Study(
                person,
                profession,
                graduationDate,
                studyModelCli.getUniversityName()
        );
    }
} 