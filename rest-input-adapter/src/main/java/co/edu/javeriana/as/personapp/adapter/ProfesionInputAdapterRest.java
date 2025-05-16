package co.edu.javeriana.as.personapp.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.mapper.ProfesionMapperRest;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class ProfesionInputAdapterRest {

    @Autowired
    @Qualifier("professionOutputAdapterMaria")
    private ProfessionOutputPort professionOutputPortMaria;

    @Autowired
    @Qualifier("professionOutputAdapterMongo")
    private ProfessionOutputPort professionOutputPortMongo;

    @Autowired
    private ProfessionInputPort professionInputPort;

    @Autowired
    private ProfesionMapperRest profesionMapperRest;

    private void setProfessionOutputPortInjection(String dbOption) throws InvalidOptionException {
        if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
            professionInputPort.setPersistence(professionOutputPortMaria);
        } else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
            professionInputPort.setPersistence(professionOutputPortMongo);
        } else {
            throw new InvalidOptionException("Invalid database option: " + dbOption);
        }
    }

    public List<ProfesionResponse> findAll(String database) {
        log.info("Into findAll Professions in Input Adapter for database: {}", database);
        try {
            setProfessionOutputPortInjection(database);
            return professionInputPort.findAll().stream()
                    .map(profession -> {
                        ProfesionResponse res = profesionMapperRest.fromDomainToResponse(profession);
                        res.setDatabase(database.toUpperCase());
                        res.setStatus("OK");
                        return res;
                    })
                    .collect(Collectors.toList());
        } catch (InvalidOptionException e) {
            log.warn("Error in findAll Professions: {}", e.getMessage());
            // Consider a way to return a list with one error response, or an empty list as is.
            return new ArrayList<>();
        }
    }

    public ProfesionResponse create(ProfesionRequest request) {
        log.info("Into create Profession in Input Adapter: {}", request);
        try {
            setProfessionOutputPortInjection(request.getDatabase());
            Profession profession = professionInputPort.create(profesionMapperRest.fromRequestToDomain(request));
            return profesionMapperRest.fromDomainToResponse(profession, request.getDatabase().toUpperCase(), "OK");
        } catch (InvalidOptionException e) {
            log.warn("Error in create Profession: {}", e.getMessage());
            return new ProfesionResponse(request.getIdentification(), request.getDatabase().toUpperCase(), "ERROR: " + e.getMessage());
        }
    }

    public ProfesionResponse findById(Integer identification, String database) {
        log.info("Into findById Profession in Input Adapter for id: {} in database: {}", identification, database);
        try {
            setProfessionOutputPortInjection(database);
            Profession profession = professionInputPort.findOne(identification);
            return profesionMapperRest.fromDomainToResponse(profession, database.toUpperCase(), "OK");
        } catch (InvalidOptionException | NoExistException e) {
            log.warn("Error in findById Profession: {}", e.getMessage());
            return new ProfesionResponse(String.valueOf(identification), database.toUpperCase(), "ERROR: " + e.getMessage());
        }
    }

    public ProfesionResponse edit(Integer identification, ProfesionRequest request) {
        log.info("Into edit Profession in Input Adapter for id: {}: {}", identification, request);
        try {
            setProfessionOutputPortInjection(request.getDatabase());
            Profession profession = professionInputPort.edit(identification, profesionMapperRest.fromRequestToDomain(request));
            return profesionMapperRest.fromDomainToResponse(profession, request.getDatabase().toUpperCase(), "OK");
        } catch (InvalidOptionException | NoExistException e) {
            log.warn("Error in edit Profession: {}", e.getMessage());
            return new ProfesionResponse(String.valueOf(identification), request.getDatabase().toUpperCase(), "ERROR: " + e.getMessage());
        }
    }

    public ProfesionResponse delete(Integer identification, String database) {
        log.info("Into delete Profession in Input Adapter for id: {} in database: {}", identification, database);
        try {
            setProfessionOutputPortInjection(database);
            professionInputPort.drop(identification);
            // For DELETE, typically a 204 No Content is returned by controller.
            // This response might be for logging or if controller needs to return a body.
            return new ProfesionResponse(String.valueOf(identification), database.toUpperCase(), "DELETED");
        } catch (InvalidOptionException | NoExistException e) {
            log.warn("Error in delete Profession: {}", e.getMessage());
            return new ProfesionResponse(String.valueOf(identification), database.toUpperCase(), "ERROR: " + e.getMessage());
        }
    }
} 