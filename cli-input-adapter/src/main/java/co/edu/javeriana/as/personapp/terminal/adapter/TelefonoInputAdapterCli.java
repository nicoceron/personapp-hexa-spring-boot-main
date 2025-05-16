package co.edu.javeriana.as.personapp.terminal.adapter;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.in.PhoneInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PhoneUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.terminal.mapper.TelefonoMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class TelefonoInputAdapterCli {

    @Autowired
    @Qualifier("phoneOutputAdapterMaria")
    private PhoneOutputPort phoneOutputPortMaria;

    @Autowired
    @Qualifier("phoneOutputAdapterMongo")
    private PhoneOutputPort phoneOutputPortMongo;

    @Autowired
    private TelefonoMapperCli telefonoMapperCli;

    @Autowired
    private PersonInputPort personInputPort; // To validate person existence

    private PhoneInputPort phoneInputPort;

    public void setPhoneOutputPortInjection(String dbOption) throws InvalidOptionException {
        if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
            phoneInputPort = new PhoneUseCase(phoneOutputPortMaria);
        } else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
            phoneInputPort = new PhoneUseCase(phoneOutputPortMongo);
        } else {
            throw new InvalidOptionException("Invalid database option: " + dbOption);
        }
    }

    public void crearTelefono(Scanner scanner) {
        log.info("Creating Phone");
        try {
            System.out.println("Enter Phone Number:");
            String number = scanner.nextLine();

            System.out.println("Enter Company:");
            String company = scanner.nextLine();

            System.out.println("Enter Owner's ID (CC):");
            Integer ownerId = safeIntegerInput(scanner);
            scanner.nextLine(); // Consume newline

            // Validate person existence
            Person owner = personInputPort.findOne(ownerId.longValue());
            if (owner == null) {
                System.out.println("Error: Owner with ID " + ownerId + " does not exist.");
                return;
            }

            Phone phone = new Phone(number, company, owner);
            phoneInputPort.create(phone);
            System.out.println("Phone created successfully.");

        } catch (InputMismatchException e) {
            log.warn("Invalid input type: {}", e.getMessage());
            System.out.println("Error: Invalid input type provided.");
        } catch (NoExistException e) {
            log.warn("Error related to person existence: {}", e.getMessage());
            System.out.println("Error: " + e.getMessage());        
        } catch (Exception e) {
            log.error("Error creating phone: {}", e.getMessage());
            System.out.println("Error creating phone: " + e.getMessage());
        }
    }

    public void obtenerTelefono(Scanner scanner) {
        log.info("Finding Phone by Number");
        try {
            System.out.println("Enter Phone Number to find:");
            String number = scanner.nextLine();
            Phone phone = phoneInputPort.findOne(number);
            TelefonoModelCli phoneModelCli = telefonoMapperCli.fromDomainToAdapterCli(phone);
            System.out.println("Found Phone: " + phoneModelCli.toString());
        } catch (NoExistException e) {
            log.warn("Phone not found: {}", e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            log.error("Error finding phone: {}", e.getMessage());
            System.out.println("Error finding phone: " + e.getMessage());
        }
    }

    public void editarTelefono(Scanner scanner) {
        log.info("Editing Phone");
        try {
            System.out.println("Enter Phone Number to edit:");
            String number = scanner.nextLine();

            // Check if phone exists
            Phone existingPhone = phoneInputPort.findOne(number);

            System.out.println("Enter new Company (leave blank to keep current: '" + existingPhone.getCompany() + "'):");
            String company = scanner.nextLine();
            if (company.trim().isEmpty()) {
                company = existingPhone.getCompany();
            }

            System.out.println("Enter new Owner's ID (CC) (leave blank to keep current: '" + existingPhone.getOwner().getIdentification() + "'):");
            String ownerIdStr = scanner.nextLine();
            Person owner = existingPhone.getOwner();
            if (!ownerIdStr.trim().isEmpty()) {
                Integer ownerId = Integer.parseInt(ownerIdStr);
                owner = personInputPort.findOne(ownerId.longValue());
                 if (owner == null) {
                    System.out.println("Error: New Owner with ID " + ownerId + " does not exist.");
                    return;
                }
            }
            
            Phone updatedPhone = new Phone(number, company, owner);
            phoneInputPort.edit(number, updatedPhone);
            System.out.println("Phone edited successfully.");

        } catch (InputMismatchException e) {
            log.warn("Invalid input type: {}", e.getMessage());
            System.out.println("Error: Invalid input type provided.");
        } catch (NoExistException e) {
            log.warn("Cannot edit phone: {}", e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            log.error("Error editing phone: {}", e.getMessage());
            System.out.println("Error editing phone: " + e.getMessage());
        }
    }

    public void eliminarTelefono(Scanner scanner) {
        log.info("Deleting Phone by Number");
        try {
            System.out.println("Enter Phone Number to delete:");
            String number = scanner.nextLine();
            phoneInputPort.drop(number);
            System.out.println("Phone with number " + number + " deleted successfully.");
        } catch (NoExistException e) {
            log.warn("Cannot delete phone, not found: {}", e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting phone: {}", e.getMessage());
            System.out.println("Error deleting phone: " + e.getMessage());
        }
    }

    public void listarTelefonos() {
        log.info("Listing all Phones");
        try {
            List<TelefonoModelCli> phones = phoneInputPort.findAll().stream()
                    .map(telefonoMapperCli::fromDomainToAdapterCli)
                    .collect(Collectors.toList());
            if (phones.isEmpty()) {
                System.out.println("No phones found.");
                return;
            }
            phones.forEach(p -> System.out.println(p.toString()));
        } catch (Exception e) {
            log.error("Error listing phones: {}", e.getMessage());
            System.out.println("Error listing phones: " + e.getMessage());
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