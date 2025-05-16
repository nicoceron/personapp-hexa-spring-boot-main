package co.edu.javeriana.as.personapp.terminal.adapter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.terminal.mapper.StudyMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.StudyModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class StudyInputAdapterCli {

    @Autowired
    private StudyInputPort studyInputPort;

    @Autowired
    private StudyMapperCli studyMapperCli;

    @Autowired
    private PersonInputPort personInputPort; // To validate person existence

    @Autowired
    private ProfessionInputPort professionInputPort; // To validate profession existence

    @Autowired
    @Qualifier("studyOutputAdapterMaria")
    private StudyOutputPort studyOutputPortMaria;

    @Autowired
    @Qualifier("studyOutputAdapterMongo")
    private StudyOutputPort studyOutputPortMongo;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; // YYYY-MM-DD

    public void setStudyOutputPort(String dbOption) throws InvalidOptionException {
        if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
            studyInputPort.setPersintence(studyOutputPortMaria);
        } else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
            studyInputPort.setPersintence(studyOutputPortMongo);
        } else {
            throw new InvalidOptionException("Invalid database option: " + dbOption);
        }
    }

    public void createStudy(Scanner scanner) {
        try {
            System.out.println("Enter Database (MARIA or MONGO):");
            String db = scanner.nextLine().toUpperCase();
            setStudyOutputPort(db);

            System.out.println("Enter Person ID (CC):");
            Integer personId = safeIntegerInput(scanner);
            scanner.nextLine(); // Consume newline
            Person person = personInputPort.findOne(personId.longValue()); // Validate person

            System.out.println("Enter Profession ID:");
            Integer professionId = safeIntegerInput(scanner);
            scanner.nextLine(); // Consume newline
            Profession profession = professionInputPort.findOne(professionId); // Validate profession

            System.out.println("Enter University Name:");
            String universityName = scanner.nextLine();

            System.out.println("Enter Graduation Date (YYYY-MM-DD, leave blank if not graduated):");
            String graduationDateStr = scanner.nextLine();
            LocalDate graduationDate = null;
            if (graduationDateStr != null && !graduationDateStr.trim().isEmpty()) {
                try {
                    graduationDate = LocalDate.parse(graduationDateStr, DATE_FORMATTER);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Please use YYYY-MM-DD.");
                    return;
                }
            }

            Study newStudy = new Study(person, profession, graduationDate, universityName);
            studyInputPort.create(newStudy);
            System.out.println("Study created successfully in " + db);

        } catch (InputMismatchException e) {
            log.warn("Invalid input type: {}", e.getMessage());
            System.out.println("Error: Invalid input type provided.");
        } catch (NoExistException e) {
            log.warn("Error creating study: {}", e.getMessage());
            System.out.println("Error: " + e.getMessage());
        } catch (InvalidOptionException e) {
            log.warn("Invalid database option: {}", e.getMessage());
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("An unexpected error occurred while creating study: ", e);
            System.out.println("An unexpected error occurred. Please check logs.");
        }
    }

    public void findStudyById(Scanner scanner) {
        try {
            System.out.println("Enter Database (MARIA or MONGO):");
            String db = scanner.nextLine().toUpperCase();
            setStudyOutputPort(db);

            System.out.println("Enter Person ID (CC) of the study to find:");
            Integer personId = safeIntegerInput(scanner);
            scanner.nextLine(); 

            System.out.println("Enter Profession ID of the study to find:");
            Integer professionId = safeIntegerInput(scanner);
            scanner.nextLine(); 

            Study study = studyInputPort.findOne(personId, professionId);
            StudyModelCli model = studyMapperCli.fromDomainToAdapterCli(study, db);
            System.out.println("Found Study: " + model);

        } catch (InputMismatchException e) {
            log.warn("Invalid input type: {}", e.getMessage());
            System.out.println("Error: Invalid input type provided.");
        } catch (NoExistException e) {
            log.warn("Error finding study: {}", e.getMessage());
            System.out.println("Error: " + e.getMessage());
        } catch (InvalidOptionException e) {
            log.warn("Invalid database option: {}", e.getMessage());
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("An unexpected error occurred while finding study: ", e);
            System.out.println("An unexpected error occurred. Please check logs.");
        }
    }

    public void listAllStudies(Scanner scanner) {
        try {
            System.out.println("Enter Database (MARIA or MONGO):");
            String db = scanner.nextLine().toUpperCase();
            setStudyOutputPort(db);

            List<Study> studies = studyInputPort.findAll();
            if (studies.isEmpty()) {
                System.out.println("No studies found in " + db + ".");
                return;
            }
            System.out.println("Studies in " + db + ":");
            studies.forEach(study -> {
                StudyModelCli model = studyMapperCli.fromDomainToAdapterCli(study, db);
                System.out.println(model);
            });
        } catch (InvalidOptionException e) {
            log.warn("Invalid database option: {}", e.getMessage());
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("An unexpected error occurred while listing studies: ", e);
            System.out.println("An unexpected error occurred. Please check logs.");
        }
    }
    
    private Integer safeIntegerInput(Scanner scanner) {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Consume the invalid input
            }
        }
    }
} 