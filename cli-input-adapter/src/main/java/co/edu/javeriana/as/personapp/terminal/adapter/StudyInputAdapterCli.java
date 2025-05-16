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
        log.info("Creating a new Study...");
        try {
            System.out.println("Enter Person ID (CC):");
            Integer personId = safeIntegerInput(scanner);
            scanner.nextLine(); // Consume newline
            Person person = personInputPort.findOne(personId.longValue()); // Validate person
            if (person == null) {
                System.out.println("Error: Person with ID " + personId + " does not exist.");
                return;
            }

            System.out.println("Enter Profession ID:");
            Integer professionId = safeIntegerInput(scanner);
            scanner.nextLine(); // Consume newline
            Profession profession = professionInputPort.findOne(professionId); // Validate profession
            if (profession == null) {
                System.out.println("Error: Profession with ID " + professionId + " does not exist.");
                return;
            }

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
            Study createdStudy = studyInputPort.create(newStudy);
            if (createdStudy != null) {
                 System.out.println("Study created successfully.");
            } else {
                System.out.println("Failed to create study.");
            }

        } catch (InputMismatchException e) {
            log.warn("Invalid input type: {}", e.getMessage());
            System.out.println("Error: Invalid input type provided.");
            if(scanner.hasNextLine()) scanner.nextLine(); // consume if anything left
        } catch (NoExistException e) {
            log.warn("Error creating study due to non-existent entity: {}", e.getMessage());
        } catch (Exception e) {
            log.error("An unexpected error occurred while creating study: ", e);
            System.out.println("An unexpected error occurred. Please check logs.");
        }
    }

    public void findStudyById(Scanner scanner) {
        log.info("Finding Study by ID...");
        try {
            System.out.println("Enter Person ID (CC) of the study to find:");
            Integer personId = safeIntegerInput(scanner);
            scanner.nextLine(); 

            System.out.println("Enter Profession ID of the study to find:");
            Integer professionId = safeIntegerInput(scanner);
            scanner.nextLine(); 

            Study study = studyInputPort.findOne(personId, professionId);
            if (study != null) {
                StudyModelCli model = studyMapperCli.fromDomainToAdapterCli(study);
                System.out.println("Found Study: " + model);
            } else {
                System.out.println("Study not found for Person ID " + personId + " and Profession ID " + professionId + ".");
            }

        } catch (InputMismatchException e) {
            log.warn("Invalid input type: {}", e.getMessage());
            System.out.println("Error: Invalid input type provided.");
            if(scanner.hasNextLine()) scanner.nextLine();
        } catch (NoExistException e) {
            log.warn("Error finding study: {}", e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            log.error("An unexpected error occurred while finding study: ", e);
            System.out.println("An unexpected error occurred. Please check logs.");
        }
    }

    public void listAllStudies(Scanner scanner) {
        log.info("Listing all Studies...");
        try {
            List<Study> studies = studyInputPort.findAll();
            if (studies.isEmpty()) {
                System.out.println("No studies found.");
                return;
            }
            System.out.println("All Studies:");
            studies.forEach(study -> {
                StudyModelCli model = studyMapperCli.fromDomainToAdapterCli(study);
                System.out.println(model);
            });
        } catch (Exception e) {
            log.error("An unexpected error occurred while listing studies: ", e);
            System.out.println("An unexpected error occurred. Please check logs.");
        }
    }

    public void editStudy(Scanner scanner) {
        log.info("Editing a Study...");
        try {
            System.out.println("Enter Person ID (CC) of the study to edit:");
            Integer personId = safeIntegerInput(scanner);
            scanner.nextLine(); // Consume newline

            System.out.println("Enter Profession ID of the study to edit:");
            Integer professionId = safeIntegerInput(scanner);
            scanner.nextLine(); // Consume newline

            Study existingStudy = studyInputPort.findOne(personId, professionId);
            if (existingStudy == null) {
                System.out.println("Error: Study not found for Person ID " + personId + " and Profession ID " + professionId + ".");
                return;
            }

            System.out.println("Current University Name: " + existingStudy.getUniversityName());
            System.out.println("Enter new University Name (or press Enter to keep current):");
            String newUniversityNameStr = scanner.nextLine();
            String finalUniversityName = (newUniversityNameStr == null || newUniversityNameStr.trim().isEmpty()) ? existingStudy.getUniversityName() : newUniversityNameStr.trim();

            String currentGradDateStr = existingStudy.getGraduationDate() != null ? existingStudy.getGraduationDate().format(DATE_FORMATTER) : "Not set";
            System.out.println("Current Graduation Date: " + currentGradDateStr);
            System.out.println("Enter new Graduation Date (YYYY-MM-DD, press Enter to keep current, or type 'clear' to remove):");
            String newGraduationDateStr = scanner.nextLine();
            LocalDate finalGraduationDate = existingStudy.getGraduationDate();

            if (newGraduationDateStr != null && !newGraduationDateStr.trim().isEmpty()) {
                if (newGraduationDateStr.trim().equalsIgnoreCase("clear")) {
                    finalGraduationDate = null;
                } else {
                    try {
                        finalGraduationDate = LocalDate.parse(newGraduationDateStr.trim(), DATE_FORMATTER);
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid date format. Study not updated. Please use YYYY-MM-DD or 'clear'.");
                        return;
                    }
                }
            }

            // Create a new Study object with the original person and profession, but updated fields
            Study updatedStudy = new Study(
                existingStudy.getPerson(), 
                existingStudy.getProfession(), 
                finalGraduationDate, 
                finalUniversityName
            );

            // Pass Person ID and Profession ID along with the updated Study object
            Study result = studyInputPort.edit(personId, professionId, updatedStudy); 
            if (result != null) {
                System.out.println("Study updated successfully.");
            } else {
                System.out.println("Failed to update study. The study might have been modified or deleted by another transaction.");
            }

        } catch (InputMismatchException e) {
            log.warn("Invalid input type: {}", e.getMessage());
            System.out.println("Error: Invalid input type provided.");
            if(scanner.hasNextLine()) scanner.nextLine(); 
        } catch (NoExistException e) {
            log.warn("Error editing study: {}", e.getMessage());
            System.out.println(e.getMessage()); // e.g., "Person or Profession for the study does not exist"
        } catch (Exception e) {
            log.error("An unexpected error occurred while editing study: ", e);
            System.out.println("An unexpected error occurred. Please check logs.");
        }
    }

    public void deleteStudy(Scanner scanner) {
        log.info("Deleting a Study...");
        try {
            System.out.println("Enter Person ID (CC) of the study to delete:");
            Integer personId = safeIntegerInput(scanner);
            scanner.nextLine(); // Consume newline

            System.out.println("Enter Profession ID of the study to delete:");
            Integer professionId = safeIntegerInput(scanner);
            scanner.nextLine(); // Consume newline

            // Optional: Confirm before deleting
            System.out.println("Are you sure you want to delete the study for Person ID " + personId + " and Profession ID " + professionId + "? (yes/no)");
            String confirmation = scanner.nextLine().trim();

            if (confirmation.equalsIgnoreCase("yes")) {
                boolean deleted = studyInputPort.drop(personId, professionId);
                if (deleted) {
                    System.out.println("Study deleted successfully.");
                } else {
                    System.out.println("Failed to delete study. It might not exist or an error occurred.");
                }
            } else {
                System.out.println("Study deletion cancelled.");
            }

        } catch (InputMismatchException e) {
            log.warn("Invalid input type: {}", e.getMessage());
            System.out.println("Error: Invalid input type provided for IDs.");
            if(scanner.hasNextLine()) scanner.nextLine();
        } catch (NoExistException e) {
            // This specific exception might not be thrown by `drop` if it just returns boolean.
            // However, keeping it in case the port implementation evolves.
            log.warn("Error deleting study (NoExistException): {}", e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            log.error("An unexpected error occurred while deleting study: ", e);
            System.out.println("An unexpected error occurred. Please check logs.");
        }
    }

    public void countStudies(Scanner scanner) {
        log.info("Counting all Studies...");
        try {
            long count = studyInputPort.count();
            System.out.println("Total number of studies: " + count);
        } catch (Exception e) {
            log.error("An unexpected error occurred while counting studies: ", e);
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