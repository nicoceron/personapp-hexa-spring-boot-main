package co.edu.javeriana.as.personapp.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mapper.StudyMapperRest;
import co.edu.javeriana.as.personapp.model.request.StudyRequest;
import co.edu.javeriana.as.personapp.model.response.StudyResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class StudyInputAdapterRest {

    @Autowired
    private StudyInputPort studyInputPort;

    @Autowired
    private StudyMapperRest studyMapperRest;

    @Autowired
    @Qualifier("studyOutputAdapterMaria")
    private StudyOutputPort studyOutputPortMaria;

    @Autowired
    @Qualifier("studyOutputAdapterMongo")
    private StudyOutputPort studyOutputPortMongo;

    public List<StudyResponse> findAll(String database) {
        log.info("Finding all studies REST for database: {}", database);
        try {
            setStudyOutputPort(database);
            return studyInputPort.findAll().stream()
                    .map(study -> studyMapperRest.fromDomainToResponse(study, database.toUpperCase(), "OK"))
                    .collect(Collectors.toList());
        } catch (InvalidOptionException e) {
            log.warn("Invalid database option for findAll studies: {}", database, e);
            // Return an empty list or a list with an error response
            // For simplicity, returning an empty list here.
            return new ArrayList<>(); 
        }
    }

    public StudyResponse findById(Integer personId, Integer professionId, String database) {
        log.info("Finding study by personId: {} and professionId: {} REST for database: {}", personId, professionId, database);
        try {
            setStudyOutputPort(database);
            Study study = studyInputPort.findOne(personId, professionId);
            return studyMapperRest.fromDomainToResponse(study, database.toUpperCase(), "OK");
        } catch (InvalidOptionException e) {
             log.warn("Invalid database option for findById study with personId {} and professionId {}: {}", personId, professionId, database, e);
            return new StudyResponse(String.valueOf(personId), String.valueOf(professionId), database.toUpperCase(), "ERROR: Invalid database option: " + database);
        } catch (NoExistException e) {
            log.warn("Study with personId {} and professionId {} not found in database {}: ", personId, professionId, database, e);
            return new StudyResponse(String.valueOf(personId), String.valueOf(professionId), database.toUpperCase(), "ERROR: " + e.getMessage());
        }
    }

    public StudyResponse create(StudyRequest request) {
        log.info("Creating study REST: {}", request);
        try {
            setStudyOutputPort(request.getDatabase());
            Study study = studyInputPort.create(studyMapperRest.fromRequestToDomain(request));
            return studyMapperRest.fromDomainToResponse(study, request.getDatabase().toUpperCase(), "OK");
        } catch (InvalidOptionException e) {
            log.warn("Invalid database option for create study: {}", request.getDatabase(), e);
            return new StudyResponse(request.getPersonId(), request.getProfessionId(), request.getDatabase().toUpperCase(), "ERROR: Invalid database option: " + request.getDatabase());
        } catch (NoExistException | IllegalArgumentException e) {
            log.error("Error creating study: ", e);
            return new StudyResponse(request.getPersonId(), request.getProfessionId(), request.getDatabase().toUpperCase(), "ERROR: " + e.getMessage());
        }
    }

    public StudyResponse edit(Integer personId, Integer professionId, StudyRequest request) {
        log.info("Editing study with personId: {} and professionId: {} REST: {}", personId, professionId, request);
        try {
            setStudyOutputPort(request.getDatabase());
            Study study = studyInputPort.edit(personId, professionId, studyMapperRest.fromRequestToDomain(request));
            return studyMapperRest.fromDomainToResponse(study, request.getDatabase().toUpperCase(), "OK");
        } catch (InvalidOptionException e) {
            log.warn("Invalid database option for edit study with personId {} and professionId {}: {}",personId, professionId, request.getDatabase(), e);
            return new StudyResponse(String.valueOf(personId), String.valueOf(professionId), request.getDatabase().toUpperCase(), "ERROR: Invalid database option: " + request.getDatabase());
        } catch (NoExistException | IllegalArgumentException e) {
            log.error("Error editing study: ", e);
            return new StudyResponse(String.valueOf(personId), String.valueOf(professionId), request.getDatabase().toUpperCase(), "ERROR: " + e.getMessage());
        }
    }

    public StudyResponse delete(Integer personId, Integer professionId, String database) {
        log.info("Deleting study with personId: {} and professionId: {} REST for database: {}", personId, professionId, database);
        try {
            setStudyOutputPort(database);
            studyInputPort.drop(personId, professionId);
            return new StudyResponse(String.valueOf(personId), String.valueOf(professionId), database.toUpperCase(), "OK, Study deleted");
        } catch (InvalidOptionException e) {
            log.warn("Invalid database option for delete study with personId {} and professionId {}: {}",personId, professionId, database, e);
            return new StudyResponse(String.valueOf(personId), String.valueOf(professionId), database.toUpperCase(), "ERROR: Invalid database option: " + database);
        } catch (NoExistException e) {
            log.error("Error deleting study: ", e);
            return new StudyResponse(String.valueOf(personId), String.valueOf(professionId), database.toUpperCase(), "ERROR: " + e.getMessage());
        }
    }

    private void setStudyOutputPort(String database) throws InvalidOptionException {
        if (DatabaseOption.MARIA.toString().equalsIgnoreCase(database)) {
            studyInputPort.setPersintence(studyOutputPortMaria);
        } else if (DatabaseOption.MONGO.toString().equalsIgnoreCase(database)) {
            studyInputPort.setPersintence(studyOutputPortMongo);
        } else {
            throw new InvalidOptionException("Invalid database option: " + database);
        }
    }
} 