package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.InputMismatchException;
import java.util.Scanner;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.terminal.adapter.StudyInputAdapterCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StudyMenu {

    // Constants for database selection
    private static final int OPCION_REGRESAR_MODULOS = 0;
    private static final int PERSISTENCIA_MARIADB = 1;
    private static final int PERSISTENCIA_MONGODB = 2;

    // Constants for study operations
    private static final int OPCION_REGRESAR_MOTOR_PERSISTENCIA = 0;
    private static final int OPCION_CREAR = 1;
    private static final int OPCION_VER_TODOS = 2;
    private static final int OPCION_BUSCAR_UNO = 3; // Find by Person ID and Profession ID
    private static final int OPCION_EDITAR = 4;
    private static final int OPCION_ELIMINAR = 5;
    private static final int OPCION_CONTAR = 6;


    public void iniciarMenu(StudyInputAdapterCli studyInputAdapterCli, Scanner scanner) {
        boolean isValidOuterLoop = false;
        do {
            try {
                mostrarMenuMotorPersistencia();
                int opcionMotor = leerOpcion(scanner);
                switch (opcionMotor) {
                    case OPCION_REGRESAR_MODULOS:
                        isValidOuterLoop = true;
                        break;
                    case PERSISTENCIA_MARIADB:
                        studyInputAdapterCli.setStudyOutputPort("MARIA");
                        menuOpcionesEstudios(studyInputAdapterCli, scanner);
                        break;
                    case PERSISTENCIA_MONGODB:
                        studyInputAdapterCli.setStudyOutputPort("MONGO");
                        menuOpcionesEstudios(studyInputAdapterCli, scanner);
                        break;
                    default:
                        log.warn("La opción de motor de persistencia elegida no es válida: {}", opcionMotor);
                        System.out.println("Opción no válida. Por favor, elija " + PERSISTENCIA_MARIADB + " para MariaDB, " + PERSISTENCIA_MONGODB + " para MongoDB, o " + OPCION_REGRESAR_MODULOS + " para regresar.");
                        break;
                }
            } catch (InvalidOptionException e) { // Catching from setStudyOutputPort
                log.warn("Error al configurar el puerto de salida: {}", e.getMessage());
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) { // General catch for other unexpected issues in this loop
                log.error("Error inesperado en el menú de selección de motor para Estudios: ", e);
                System.out.println("Ocurrió un error inesperado. Por favor, intente de nuevo.");
            }
        } while (!isValidOuterLoop);
    }

    private void menuOpcionesEstudios(StudyInputAdapterCli studyInputAdapterCli, Scanner scanner) {
        boolean isValidInnerLoop = false;
        do {
            try {
                mostrarMenuOpcionesEstudios();
                int opcionEstudios = leerOpcion(scanner);
                switch (opcionEstudios) {
                    case OPCION_REGRESAR_MOTOR_PERSISTENCIA:
                        isValidInnerLoop = true;
                        break;
                    case OPCION_CREAR:
                        studyInputAdapterCli.createStudy(scanner);
                        break;
                    case OPCION_VER_TODOS:
                        studyInputAdapterCli.listAllStudies(scanner);
                        break;
                    case OPCION_BUSCAR_UNO:
                        studyInputAdapterCli.findStudyById(scanner);
                        break;
                    case OPCION_EDITAR:
                        studyInputAdapterCli.editStudy(scanner);
                        break;
                    case OPCION_ELIMINAR:
                        studyInputAdapterCli.deleteStudy(scanner);
                        break;
                    case OPCION_CONTAR:
                        studyInputAdapterCli.countStudies(scanner);
                        break;
                    default:
                        log.warn("Opción inválida '{}' ingresada en el menú de estudios", opcionEstudios);
                        System.out.println("Opción no válida. Por favor, intente de nuevo.");
                        break;
                }
            } catch (Exception e) { // General catch for unexpected issues in study operations menu
                log.error("Error en el menú de opciones de estudios: ", e);
                System.out.println("Ocurrió un error inesperado en las operaciones de estudios: " + e.getMessage());
                // It's often good to ensure scanner is in a good state if an error occurs before next read
                if (scanner.hasNextLine()) { // Check if there's anything to consume
                    scanner.nextLine(); // Consume the rest of the line to prevent infinite loops on bad input
                }
            }
        } while (!isValidInnerLoop);
    }

    private void mostrarMenuMotorPersistencia() {
        System.out.println("\n--- Seleccione Motor de Persistencia para Estudios ---");
        System.out.println(PERSISTENCIA_MARIADB + " para MariaDB");
        System.out.println(PERSISTENCIA_MONGODB + " para MongoDB");
        System.out.println(OPCION_REGRESAR_MODULOS + " para Regresar al Menú Principal");
    }

    private void mostrarMenuOpcionesEstudios() {
        System.out.println("\n--- Menú Estudios ---");
        System.out.println(OPCION_CREAR + " para Crear Estudio");
        System.out.println(OPCION_VER_TODOS + " para Ver Todos los Estudios");
        System.out.println(OPCION_BUSCAR_UNO + " para Buscar Estudio por IDs");
        System.out.println(OPCION_EDITAR + " para Editar Estudio");
        System.out.println(OPCION_ELIMINAR + " para Eliminar Estudio");
        System.out.println(OPCION_CONTAR + " para Contar Estudios");
        System.out.println(OPCION_REGRESAR_MOTOR_PERSISTENCIA + " para Regresar al Menú de Persistencia");
    }

    private int leerOpcion(Scanner keyboard) {
        try {
            System.out.print("Ingrese una opción: ");
            int opcion = keyboard.nextInt();
            keyboard.nextLine(); // consume the rest of the line after the number
            return opcion;
        } catch (InputMismatchException e) {
            log.warn("Entrada inválida, solo se permiten números.");
            keyboard.nextLine(); // consume the invalid input fully
            return -1; // Indicate invalid input to re-prompt in the calling menu loop
        }
    }
} 