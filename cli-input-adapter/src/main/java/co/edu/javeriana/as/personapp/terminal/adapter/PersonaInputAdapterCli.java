package co.edu.javeriana.as.personapp.terminal.adapter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Scanner;
import java.util.InputMismatchException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PersonUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.terminal.mapper.PersonaMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.PersonaModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class PersonaInputAdapterCli {

	@Autowired
	@Qualifier("personOutputAdapterMaria")
	private PersonOutputPort personOutputPortMaria;

	@Autowired
	@Qualifier("personOutputAdapterMongo")
	private PersonOutputPort personOutputPortMongo;

	@Autowired
	private PersonaMapperCli personaMapperCli;

	PersonInputPort personInputPort;

	public void setPersonOutputPortInjection(String dbOption) throws InvalidOptionException {
		if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
			personInputPort = new PersonUseCase(personOutputPortMaria);
		} else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
			personInputPort = new PersonUseCase(personOutputPortMongo);
		} else {
			throw new InvalidOptionException("Invalid database option: " + dbOption);
		}
	}

	public void historial1() {
		log.info("Into historial PersonaEntity in Input Adapter");
		List<PersonaModelCli> persona = personInputPort.findAll().stream().map(personaMapperCli::fromDomainToAdapterCli)
					.collect(Collectors.toList());
		persona.forEach(p -> System.out.println(p.toString()));
	}
	public void historial() {
	    log.info("Into historial PersonaEntity in Input Adapter");
	    personInputPort.findAll().stream()
	        .map(personaMapperCli::fromDomainToAdapterCli)
	        .forEach(System.out::println);
	}

	public void crearPersona(Scanner keyboard) {
		log.info("Creando Persona...");
		try {
			System.out.print("Ingrese el CC de la persona: ");
			int cc = keyboard.nextInt();
			keyboard.nextLine(); // Consume newline

			System.out.print("Ingrese el Nombre de la persona: ");
			String nombre = keyboard.nextLine();

			System.out.print("Ingrese el Apellido de la persona: ");
			String apellido = keyboard.nextLine();

			System.out.print("Ingrese el Género de la persona (MALE, FEMALE, OTHER): ");
			String generoStr = keyboard.nextLine().toUpperCase();

			System.out.print("Ingrese la Edad de la persona: ");
			int edad = keyboard.nextInt();
			keyboard.nextLine(); // Consume newline

			PersonaModelCli personaModelCli = new PersonaModelCli(cc, nombre, apellido, generoStr, edad);
			Person personDomain = personaMapperCli.fromAdapterToDomain(personaModelCli);

			Person createdPerson = personInputPort.create(personDomain);
			if (createdPerson != null) {
				System.out.println("Persona creada exitosamente: " + personaMapperCli.fromDomainToAdapterCli(createdPerson).toString());
			} else {
				System.out.println("Error al crear la persona.");
			}
		} catch (InputMismatchException e) {
			log.warn("Entrada inválida por parte del usuario.", e);
			System.out.println("Error: Tipo de dato incorrecto. Por favor, intente de nuevo.");
			keyboard.nextLine(); // Ensure scanner is clear after mismatch
		} catch (Exception e) {
			log.error("Error al crear la persona: ", e);
			System.out.println("Ocurrió un error inesperado al crear la persona: " + e.getMessage());
		}
	}

	public void buscarPersonaPorId(Scanner keyboard) {
		log.info("Buscando Persona por ID...");
		try {
			System.out.print("Ingrese el CC de la persona a buscar: ");
			int ccInt = keyboard.nextInt();
			keyboard.nextLine(); // Consume newline
			Long cc = (long) ccInt; // Cast to Long

			Person personDomain = personInputPort.findOne(cc);
			if (personDomain != null) {
				System.out.println("Persona encontrada: " + personaMapperCli.fromDomainToAdapterCli(personDomain).toString());
			} else {
				// This case might not be reached if findOne throws NoExistException
				System.out.println("Persona con CC " + cc + " no encontrada.");
			}
		} catch (InputMismatchException e) {
			log.warn("Entrada inválida por parte del usuario.", e);
			System.out.println("Error: Tipo de dato incorrecto para el CC. Por favor, intente de nuevo.");
			keyboard.nextLine(); // Ensure scanner is clear after mismatch
		} catch (co.edu.javeriana.as.personapp.common.exceptions.NoExistException e) {
		    log.warn("No se encontró la persona con el CC especificado: " + e.getMessage());
		    System.out.println(e.getMessage()); 
		} catch (Exception e) {
			log.error("Error al buscar la persona: ", e);
			System.out.println("Ocurrió un error inesperado al buscar la persona: " + e.getMessage());
		}
	}

	public void editarPersona(Scanner keyboard) {
		log.info("Editando Persona...");
		try {
			System.out.print("Ingrese el CC de la persona a editar: ");
			int ccInt = keyboard.nextInt();
			keyboard.nextLine(); // Consume newline
			Long cc = (long) ccInt;

			Person personToEdit = personInputPort.findOne(cc);
			if (personToEdit == null) {
				System.out.println("Persona con CC " + cc + " no encontrada. No se puede editar.");
				return;
			}

			System.out.println("Datos actuales: " + personaMapperCli.fromDomainToAdapterCli(personToEdit).toString());

			System.out.print("Nuevo Nombre (deje en blanco para no cambiar): ");
			String nombre = keyboard.nextLine();
			if (nombre.isEmpty()) {
				nombre = personToEdit.getFirstName();
			}

			System.out.print("Nuevo Apellido (deje en blanco para no cambiar): ");
			String apellido = keyboard.nextLine();
			if (apellido.isEmpty()) {
				apellido = personToEdit.getLastName();
			}

			System.out.print("Nuevo Género (MALE, FEMALE, OTHER, deje en blanco para no cambiar): ");
			String generoStr = keyboard.nextLine().toUpperCase();
			if (generoStr.isEmpty()) {
				generoStr = personToEdit.getGender().toString();
			}

			System.out.print("Nueva Edad (deje en blanco para no cambiar o ingrese -1): ");
			String edadStr = keyboard.nextLine();
			int edad;
			if (edadStr.isEmpty() || edadStr.equals("-1")) {
				edad = personToEdit.getAge() != null ? personToEdit.getAge() : 0; // Default if null
			} else {
				try {
					edad = Integer.parseInt(edadStr);
				} catch (NumberFormatException e) {
					System.out.println("Edad inválida. Se mantendrá la edad actual.");
					edad = personToEdit.getAge() != null ? personToEdit.getAge() : 0;
				}
			}

			PersonaModelCli personaModelCli = new PersonaModelCli(ccInt, nombre, apellido, generoStr, edad);
			Person updatedPersonDomain = personaMapperCli.fromAdapterToDomain(personaModelCli);

			Person resultPerson = personInputPort.edit(cc, updatedPersonDomain);
			if (resultPerson != null) {
				System.out.println("Persona actualizada exitosamente: " + personaMapperCli.fromDomainToAdapterCli(resultPerson).toString());
			} else {
				System.out.println("Error al actualizar la persona.");
			}

		} catch (InputMismatchException e) {
			log.warn("Entrada inválida por parte del usuario.", e);
			System.out.println("Error: Tipo de dato incorrecto para el CC. Por favor, intente de nuevo.");
			keyboard.nextLine(); // Ensure scanner is clear after mismatch
		} catch (co.edu.javeriana.as.personapp.common.exceptions.NoExistException e) {
            log.warn("No se encontró la persona con el CC especificado para editar: " + e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
			log.error("Error al editar la persona: ", e);
			System.out.println("Ocurrió un error inesperado al editar la persona: " + e.getMessage());
		}
	}

	public void eliminarPersona(Scanner keyboard) {
		log.info("Eliminando Persona...");
		try {
			System.out.print("Ingrese el CC de la persona a eliminar: ");
			int ccInt = keyboard.nextInt();
			keyboard.nextLine(); // Consume newline
			Long cc = (long) ccInt;

			boolean deleted = personInputPort.drop(cc);
			if (deleted) {
				System.out.println("Persona con CC " + cc + " eliminada exitosamente.");
			} else {
				// This might not be reached if NoExistException is thrown first
				System.out.println("Error al eliminar la persona con CC " + cc + ". Puede que no exista.");
			}
		} catch (InputMismatchException e) {
			log.warn("Entrada inválida por parte del usuario.", e);
			System.out.println("Error: Tipo de dato incorrecto para el CC. Por favor, intente de nuevo.");
			keyboard.nextLine(); // Ensure scanner is clear after mismatch
		} catch (co.edu.javeriana.as.personapp.common.exceptions.NoExistException e) {
            log.warn("No se encontró la persona con el CC especificado para eliminar: " + e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
			log.error("Error al eliminar la persona: ", e);
			System.out.println("Ocurrió un error inesperado al eliminar la persona: " + e.getMessage());
		}
	}

	public void contarPersonas() {
		log.info("Contando Personas...");
		try {
			int count = personInputPort.count();
			System.out.println("Número total de personas: " + count);
		} catch (Exception e) {
			log.error("Error al contar las personas: ", e);
			System.out.println("Ocurrió un error inesperado al contar las personas: " + e.getMessage());
		}
	}

}
