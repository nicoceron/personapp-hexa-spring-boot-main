package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.InputMismatchException;
import java.util.Scanner;

import co.edu.javeriana.as.personapp.terminal.adapter.StudyInputAdapterCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StudyMenu {

    private static final String OPCION_REGRESAR_MODULOS = "0";
    private static final String OPCION_REGRESAR_MOTOR_PERSISTENCIA = "0";

    private static final String OPCION_VER_TODOS = "1";
    private static final String OPCION_BUSCAR_UNO = "2";
    private static final String OPCION_CREAR = "3";
    // Update and Delete for studies are more complex due to composite keys and relationships.
    // For simplicity in CLI, we might omit them or implement them carefully.
    // We will implement a simple create, find one, and find all for now.

    public void iniciarMenu(StudyInputAdapterCli studyInputAdapterCli, Scanner scanner) {
        boolean validInput = false;
        do {
            try {
                mostrarMenuMotorPersistencia();
                String opcionMotor = scanner.nextLine();
                if (OPCION_REGRESAR_MODULOS.equals(opcionMotor)) {
                    validInput = true; // Exit this menu
                } else {
                    studyInputAdapterCli.setStudyOutputPort(opcionMotor);
                    menuOpcionesEstudios(studyInputAdapterCli, scanner);
                    validInput = true; // Assume operations inside menuOpcionesEstudios handle their loops
                }
            } catch (InputMismatchException e) {
                log.warn("Invalid input type: {}", e.getMessage());
                System.out.println("Error: Por favor, ingrese un número válido para la opción.");
                scanner.nextLine(); // Consume the invalid input
            } catch (Exception e) {
                log.error("Error en el menú de estudios: ", e);
                System.out.println("Ocurrió un error inesperado: " + e.getMessage());
            }
        } while (!validInput);
    }

    private void menuOpcionesEstudios(StudyInputAdapterCli studyInputAdapterCli, Scanner scanner) {
        boolean validInput = false;
        do {
            try {
                mostrarMenuOpcionesEstudios();
                String opcionEstudios = scanner.nextLine();
                switch (opcionEstudios) {
                    case OPCION_REGRESAR_MOTOR_PERSISTENCIA:
                        validInput = true; // Go back to motor selection
                        break;
                    case OPCION_VER_TODOS:
                        studyInputAdapterCli.listAllStudies(scanner);
                        break;
                    case OPCION_BUSCAR_UNO:
                        studyInputAdapterCli.findStudyById(scanner);
                        break;
                    case OPCION_CREAR:
                        studyInputAdapterCli.createStudy(scanner);
                        break;
                    default:
                        log.warn("Opción inválida '{}' ingresada en el menú de estudios", opcionEstudios);
                        System.out.println("Opción no válida. Por favor, intente de nuevo.");
                        break;
                }
            } catch (InputMismatchException e) {
                log.warn("Invalid input type in study options menu: {}", e.getMessage());
                System.out.println("Error: Por favor, ingrese un número válido para la opción.");
                scanner.nextLine(); // Consume the invalid input
            } catch (Exception e) {
                log.error("Error en el menú de opciones de estudios: ", e);
                System.out.println("Ocurrió un error inesperado: " + e.getMessage());
            }
        } while (!validInput);
    }

    private void mostrarMenuMotorPersistencia() {
        System.out.println("-----------------------");
        System.out.println("SELECCIONE EL MOTOR DE PERSISTENCIA PARA ESTUDIOS");
        System.out.println("1. MariaDB");
        System.out.println("2. MongoDB");
        System.out.println("0. Regresar al menú de módulos");
        System.out.println("-----------------------");
    }

    private void mostrarMenuOpcionesEstudios() {
        System.out.println("-----------------------");
        System.out.println("OPCIONES DEL MÓDULO DE ESTUDIOS");
        System.out.println(OPCION_VER_TODOS + ". Ver todos los estudios");
        System.out.println(OPCION_BUSCAR_UNO + ". Buscar un estudio por ID Persona y ID Profesión");
        System.out.println(OPCION_CREAR + ". Crear un nuevo estudio");
        System.out.println(OPCION_REGRESAR_MOTOR_PERSISTENCIA + ". Regresar a selección de motor de persistencia");
        System.out.println("-----------------------");
    }
} 