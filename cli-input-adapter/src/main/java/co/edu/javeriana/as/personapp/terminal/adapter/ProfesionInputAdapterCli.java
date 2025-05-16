package co.edu.javeriana.as.personapp.terminal.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.ProfessionUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.terminal.mapper.ProfesionMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.ProfesionModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class ProfesionInputAdapterCli {

    @Autowired
    @Qualifier("professionOutputAdapterMaria")
    private ProfessionOutputPort professionOutputPortMaria;

    @Autowired
    @Qualifier("professionOutputAdapterMongo")
    private ProfessionOutputPort professionOutputPortMongo;

    @Autowired
    private ProfesionMapperCli profesionMapperCli; // Assuming this will be created

    ProfessionInputPort professionInputPort;

    public void setProfessionOutputPortInjection(String dbOption) throws InvalidOptionException {
        if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
            professionInputPort = new ProfessionUseCase(professionOutputPortMaria);
        } else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
            professionInputPort = new ProfessionUseCase(professionOutputPortMongo);
        } else {
            throw new InvalidOptionException("Invalid database option: " + dbOption);
        }
    }

    public void crearProfesion(ProfesionModelCli profesionModelCli) {
        log.info("Creating Profession: {}", profesionModelCli);
        try {
            Profession profession = profesionMapperCli.fromAdapterCliToDomain(profesionModelCli);
            professionInputPort.create(profession);
            System.out.println("Profession created successfully.");
        } catch (Exception e) {
            log.error("Error creating profession: {}", e.getMessage());
            System.out.println("Error creating profession: " + e.getMessage());
        }
    }

    public void obtenerProfesion(Integer id) {
        log.info("Finding Profession by ID: {}", id);
        try {
            Profession profession = professionInputPort.findOne(id);
            ProfesionModelCli profesionModelCli = profesionMapperCli.fromDomainToAdapterCli(profession);
            System.out.println("Found Profession: " + profesionModelCli.toString());
        } catch (NoExistException e) {
            log.warn("Profession not found: {}", e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            log.error("Error finding profession: {}", e.getMessage());
            System.out.println("Error finding profession: " + e.getMessage());
        }
    }

    public void editarProfesion(ProfesionModelCli profesionModelCli) {
        log.info("Editing Profession: {}", profesionModelCli);
        try {
            Profession profession = profesionMapperCli.fromAdapterCliToDomain(profesionModelCli);
            professionInputPort.edit(profession.getIdentification(), profession);
            System.out.println("Profession edited successfully.");
        } catch (NoExistException e) {
            log.warn("Cannot edit profession, not found: {}", e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            log.error("Error editing profession: {}", e.getMessage());
            System.out.println("Error editing profession: " + e.getMessage());
        }
    }

    public void eliminarProfesion(Integer id) {
        log.info("Deleting Profession by ID: {}", id);
        try {
            professionInputPort.drop(id);
            System.out.println("Profession with ID " + id + " deleted successfully.");
        } catch (NoExistException e) {
            log.warn("Cannot delete profession, not found: {}", e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting profession: {}", e.getMessage());
            System.out.println("Error deleting profession: " + e.getMessage());
        }
    }

    public void listarProfesiones() {
        log.info("Listing all Professions");
        try {
            List<ProfesionModelCli> profesiones = professionInputPort.findAll().stream()
                    .map(profesionMapperCli::fromDomainToAdapterCli)
                    .collect(Collectors.toList());
            if (profesiones.isEmpty()) {
                System.out.println("No professions found.");
                return;
            }
            profesiones.forEach(p -> System.out.println(p.toString()));
        } catch (Exception e) {
            log.error("Error listing professions: {}", e.getMessage());
            System.out.println("Error listing professions: " + e.getMessage());
        }
    }

    public void contarProfesiones() {
        log.info("Counting all Professions");
        try {
            System.out.println("Total professions: " + professionInputPort.count());
        } catch (Exception e) {
            log.error("Error counting professions: {}", e.getMessage());
            System.out.println("Error counting professions: " + e.getMessage());
        }
    }
} 