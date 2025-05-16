package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.InputMismatchException;
import java.util.Scanner;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.terminal.adapter.PersonaInputAdapterCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PersonaMenu {

	private static final int OPCION_REGRESAR_MODULOS = 0;
	private static final int PERSISTENCIA_MARIADB = 1;
	private static final int PERSISTENCIA_MONGODB = 2;

	private static final int OPCION_REGRESAR_MOTOR_PERSISTENCIA = 0;
	private static final int OPCION_VER_TODO = 1;
	private static final int OPCION_CREAR = 2;
	private static final int OPCION_BUSCAR_POR_ID = 3;
	private static final int OPCION_EDITAR = 4;
	private static final int OPCION_ELIMINAR = 5;
	private static final int OPCION_CONTAR = 6;

	public void iniciarMenu(PersonaInputAdapterCli personaInputAdapterCli, Scanner keyboard) {
		boolean isValid = false;
		do {
			try {
				mostrarMenuMotorPersistencia();
				int opcion = leerOpcion(keyboard);
				switch (opcion) {
				case OPCION_REGRESAR_MODULOS:
					isValid = true;
					break;
				case PERSISTENCIA_MARIADB:
					personaInputAdapterCli.setPersonOutputPortInjection("MARIA");
					menuOpciones(personaInputAdapterCli,keyboard);
					break;
				case PERSISTENCIA_MONGODB:
					personaInputAdapterCli.setPersonOutputPortInjection("MONGO");
					menuOpciones(personaInputAdapterCli,keyboard);
					break;
				default:
					log.warn("La opción elegida no es válida.");
				}
			}  catch (InvalidOptionException e) {
				log.warn(e.getMessage());
			}
		} while (!isValid);
	}

	private void menuOpciones(PersonaInputAdapterCli personaInputAdapterCli, Scanner keyboard) {
		boolean isValid = false;
		do {
			try {
				mostrarMenuOpciones();
				int opcion = leerOpcion(keyboard);
				switch (opcion) {
				case OPCION_REGRESAR_MOTOR_PERSISTENCIA:
					isValid = true;
					break;
				case OPCION_VER_TODO:
					personaInputAdapterCli.historial();					
					break;
				case OPCION_CREAR:
					personaInputAdapterCli.crearPersona(keyboard);
					break;
				case OPCION_BUSCAR_POR_ID:
					personaInputAdapterCli.buscarPersonaPorId(keyboard);
					break;
				case OPCION_EDITAR:
					personaInputAdapterCli.editarPersona(keyboard);
					break;
				case OPCION_ELIMINAR:
					personaInputAdapterCli.eliminarPersona(keyboard);
					break;
				case OPCION_CONTAR:
					personaInputAdapterCli.contarPersonas();
					break;
				default:
					log.warn("La opción elegida no es válida.");
				}
			} catch (InputMismatchException e) {
				log.warn("Solo se permiten números.");
				keyboard.nextLine(); // Consume the invalid input
			} catch (Exception e) {
                log.error("Error en el menú de opciones de persona: {}", e.getMessage(), e);
                System.out.println("Ocurrió un error inesperado: " + e.getMessage());
            }
		} while (!isValid);
	}

	private void mostrarMenuOpciones() {
		System.out.println("----------------------");
		System.out.println("--- Menú Personas ---");
		System.out.println(OPCION_VER_TODO + " para Ver Todas las Personas");
		System.out.println(OPCION_CREAR + " para Crear Persona");
		System.out.println(OPCION_BUSCAR_POR_ID + " para Buscar Persona por ID");
		System.out.println(OPCION_EDITAR + " para Editar Persona");
		System.out.println(OPCION_ELIMINAR + " para Eliminar Persona");
		System.out.println(OPCION_CONTAR + " para Contar Personas");
		System.out.println(OPCION_REGRESAR_MOTOR_PERSISTENCIA + " para Regresar al Menú de Persistencia");
	}

	private void mostrarMenuMotorPersistencia() {
		System.out.println("----------------------");
		System.out.println("--- Motor de Persistencia para Personas ---");
		System.out.println(PERSISTENCIA_MARIADB + " para MariaDB");
		System.out.println(PERSISTENCIA_MONGODB + " para MongoDB");
		System.out.println(OPCION_REGRESAR_MODULOS + " para regresar");
	}

	private int leerOpcion(Scanner keyboard) {
		// Removed redundant try-catch, will be caught by menuOpciones
		// It's better to handle the exception in the loop that can retry.
		System.out.print("Ingrese una opción: ");
		int opcion = keyboard.nextInt();
		keyboard.nextLine(); // Consume newline left-over
		return opcion;
	}

}
