package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.InputMismatchException;
import java.util.Scanner;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.terminal.adapter.TelefonoInputAdapterCli;
// TelefonoModelCli might not be directly used for creation if done step-by-step in adapter
// import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TelefonoMenu {

    private static final int OPCION_REGRESAR_MODULOS = 0;
    private static final int PERSISTENCIA_MARIADB = 1;
    private static final int PERSISTENCIA_MONGODB = 2;

    private static final int OPCION_REGRESAR_MOTOR_PERSISTENCIA = 0;
    private static final int OPCION_CREAR = 1;
    private static final int OPCION_VER_TODOS = 2;
    private static final int OPCION_BUSCAR_POR_NUMERO = 3;
    private static final int OPCION_EDITAR = 4;
    private static final int OPCION_ELIMINAR = 5;
    // Count might be less relevant for phones unless we want total phones in system.

    public void iniciarMenu(TelefonoInputAdapterCli telefonoInputAdapterCli, Scanner keyboard) {
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
                        telefonoInputAdapterCli.setPhoneOutputPortInjection("MARIA");
                        menuOpciones(telefonoInputAdapterCli, keyboard);
                        break;
                    case PERSISTENCIA_MONGODB:
                        telefonoInputAdapterCli.setPhoneOutputPortInjection("MONGO");
                        menuOpciones(telefonoInputAdapterCli, keyboard);
                        break;
                    default:
                        log.warn("La opción elegida no es válida.");
                        System.out.println("Opción no válida. Intente de nuevo.");
                }
            } catch (InvalidOptionException e) {
                log.warn("Error de persistencia: {}", e.getMessage());
                System.out.println("Error con la base de datos seleccionada: " + e.getMessage());
            } catch (Exception e) {
                log.error("Error inesperado en menu telefonos: ", e);
                System.out.println("Ocurrió un error inesperado. Por favor, revise los logs.");
            }
        } while (!isValid);
    }

    private void menuOpciones(TelefonoInputAdapterCli telefonoInputAdapterCli, Scanner keyboard) {
        boolean isValid = false;
        do {
            try {
                mostrarMenuOpciones();
                int opcion = leerOpcion(keyboard);
                switch (opcion) {
                    case OPCION_REGRESAR_MOTOR_PERSISTENCIA:
                        isValid = true;
                        break;
                    case OPCION_CREAR:
                        telefonoInputAdapterCli.crearTelefono(keyboard); // Adapter handles input
                        break;
                    case OPCION_VER_TODOS:
                        telefonoInputAdapterCli.listarTelefonos();
                        break;
                    case OPCION_BUSCAR_POR_NUMERO:
                         telefonoInputAdapterCli.obtenerTelefono(keyboard); // Adapter handles input
                        break;
                    case OPCION_EDITAR:
                        telefonoInputAdapterCli.editarTelefono(keyboard); // Adapter handles input
                        break;
                    case OPCION_ELIMINAR:
                        telefonoInputAdapterCli.eliminarTelefono(keyboard); // Adapter handles input
                        break;
                    default:
                        log.warn("La opción elegida no es válida.");
                        System.out.println("Opción no válida. Intente de nuevo.");
                }
            } catch (InputMismatchException e) {
                log.warn("Solo se permiten números.");
                System.out.println("Entrada inválida. Por favor, ingrese un número.");
                keyboard.nextLine(); // Consume the invalid input fully
            } catch (Exception e) {
                log.error("Error en el menú de opciones de teléfono: {}", e.getMessage());
                System.out.println("Ocurrió un error inesperado: " + e.getMessage());
            }
        } while (!isValid);
    }

    private void mostrarMenuOpciones() {
        System.out.println("\n--- Menú Teléfonos ---");
        System.out.println(OPCION_CREAR + " para Crear Teléfono");
        System.out.println(OPCION_VER_TODOS + " para Ver Todos los Teléfonos");
        System.out.println(OPCION_BUSCAR_POR_NUMERO + " para Buscar Teléfono por Número");
        System.out.println(OPCION_EDITAR + " para Editar Teléfono");
        System.out.println(OPCION_ELIMINAR + " para Eliminar Teléfono");
        System.out.println(OPCION_REGRESAR_MOTOR_PERSISTENCIA + " para Regresar al Menú de Persistencia");
    }

    private void mostrarMenuMotorPersistencia() {
        System.out.println("\n--- Seleccione Motor de Persistencia para Teléfonos ---");
        System.out.println(PERSISTENCIA_MARIADB + " para MariaDB");
        System.out.println(PERSISTENCIA_MONGODB + " para MongoDB");
        System.out.println(OPCION_REGRESAR_MODULOS + " para Regresar al Menú Principal");
    }

    private int leerOpcion(Scanner keyboard) {
        try {
            System.out.print("Ingrese una opción: ");
            int opcion = keyboard.nextInt();
            keyboard.nextLine(); // consume the rest of the line after the number
            return opcion;
        } catch (InputMismatchException e) {
            log.warn("Solo se permiten números.");
            System.out.println("Entrada inválida. Por favor, ingrese un número.");
            keyboard.nextLine(); // consume the invalid input fully
            return -1; // Indicate invalid input to re-prompt
        }
    }
} 