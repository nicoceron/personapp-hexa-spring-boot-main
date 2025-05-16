package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.InputMismatchException;
import java.util.Scanner;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.terminal.adapter.ProfesionInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.model.ProfesionModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProfesionMenu {

    private static final int OPCION_REGRESAR_MODULOS = 0;
    private static final int PERSISTENCIA_MARIADB = 1;
    private static final int PERSISTENCIA_MONGODB = 2;

    private static final int OPCION_REGRESAR_MOTOR_PERSISTENCIA = 0;
    private static final int OPCION_CREAR = 1;
    private static final int OPCION_VER_TODOS = 2;
    private static final int OPCION_BUSCAR_POR_ID = 3;
    private static final int OPCION_EDITAR = 4;
    private static final int OPCION_ELIMINAR = 5;
    private static final int OPCION_CONTAR = 6; // Added option to count professions

    public void iniciarMenu(ProfesionInputAdapterCli profesionInputAdapterCli, Scanner keyboard) {
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
                        profesionInputAdapterCli.setProfessionOutputPortInjection("MARIA");
                        menuOpciones(profesionInputAdapterCli, keyboard);
                        break;
                    case PERSISTENCIA_MONGODB:
                        profesionInputAdapterCli.setProfessionOutputPortInjection("MONGO");
                        menuOpciones(profesionInputAdapterCli, keyboard);
                        break;
                    default:
                        log.warn("La opción elegida no es válida.");
                }
            } catch (InvalidOptionException e) {
                log.warn(e.getMessage());
            }
        } while (!isValid);
    }

    private void menuOpciones(ProfesionInputAdapterCli profesionInputAdapterCli, Scanner keyboard) {
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
                        crearProfesion(profesionInputAdapterCli, keyboard);
                        break;
                    case OPCION_VER_TODOS:
                        profesionInputAdapterCli.listarProfesiones();
                        break;
                    case OPCION_BUSCAR_POR_ID:
                        buscarProfesionPorId(profesionInputAdapterCli, keyboard);
                        break;
                    case OPCION_EDITAR:
                        editarProfesion(profesionInputAdapterCli, keyboard);
                        break;
                    case OPCION_ELIMINAR:
                        eliminarProfesion(profesionInputAdapterCli, keyboard);
                        break;
                    case OPCION_CONTAR:
                        profesionInputAdapterCli.contarProfesiones();
                        break;
                    default:
                        log.warn("La opción elegida no es válida.");
                }
            } catch (InputMismatchException e) {
                log.warn("Solo se permiten números.");
                keyboard.nextLine(); // Consume the invalid input
            } catch (Exception e) {
                log.error("Error en el menú de opciones de profesión: {}", e.getMessage());
            }
        } while (!isValid);
    }

    private void crearProfesion(ProfesionInputAdapterCli adapter, Scanner keyboard) {
        try {
            System.out.print("Ingrese ID de la profesión: ");
            Integer id = keyboard.nextInt();
            keyboard.nextLine(); // consume newline
            System.out.print("Ingrese nombre de la profesión: ");
            String nombre = keyboard.nextLine();
            System.out.print("Ingrese descripción de la profesión (opcional): ");
            String descripcion = keyboard.nextLine();
            adapter.crearProfesion(new ProfesionModelCli(id, nombre, descripcion.isEmpty() ? null : descripcion));
        } catch (InputMismatchException e) {
            log.warn("Entrada inválida. Por favor ingrese los datos correctos.");
            keyboard.nextLine(); // Consume the invalid input
        }
    }

    private void buscarProfesionPorId(ProfesionInputAdapterCli adapter, Scanner keyboard) {
        try {
            System.out.print("Ingrese ID de la profesión a buscar: ");
            Integer id = keyboard.nextInt();
            keyboard.nextLine(); // consume newline
            adapter.obtenerProfesion(id);
        } catch (InputMismatchException e) {
            log.warn("ID inválido. Por favor ingrese un número.");
            keyboard.nextLine(); // Consume the invalid input
        }
    }

    private void editarProfesion(ProfesionInputAdapterCli adapter, Scanner keyboard) {
        try {
            System.out.print("Ingrese ID de la profesión a editar: ");
            Integer id = keyboard.nextInt();
            keyboard.nextLine(); // consume newline
            System.out.print("Ingrese nuevo nombre de la profesión: ");
            String nombre = keyboard.nextLine();
            System.out.print("Ingrese nueva descripción de la profesión (opcional): ");
            String descripcion = keyboard.nextLine();
            adapter.editarProfesion(new ProfesionModelCli(id, nombre, descripcion.isEmpty() ? null : descripcion));
        } catch (InputMismatchException e) {
            log.warn("Entrada inválida. Por favor ingrese los datos correctos.");
            keyboard.nextLine(); // Consume the invalid input
        }
    }

    private void eliminarProfesion(ProfesionInputAdapterCli adapter, Scanner keyboard) {
        try {
            System.out.print("Ingrese ID de la profesión a eliminar: ");
            Integer id = keyboard.nextInt();
            keyboard.nextLine(); // consume newline
            adapter.eliminarProfesion(id);
        } catch (InputMismatchException e) {
            log.warn("ID inválido. Por favor ingrese un número.");
            keyboard.nextLine(); // Consume the invalid input
        }
    }

    private void mostrarMenuOpciones() {
        System.out.println("\n--- Menú Profesiones ---");
        System.out.println(OPCION_CREAR + " para Crear Profesión");
        System.out.println(OPCION_VER_TODOS + " para Ver Todas las Profesiones");
        System.out.println(OPCION_BUSCAR_POR_ID + " para Buscar Profesión por ID");
        System.out.println(OPCION_EDITAR + " para Editar Profesión");
        System.out.println(OPCION_ELIMINAR + " para Eliminar Profesión");
        System.out.println(OPCION_CONTAR + " para Contar Profesiones");
        System.out.println(OPCION_REGRESAR_MOTOR_PERSISTENCIA + " para Regresar al Menú de Persistencia");
    }

    private void mostrarMenuMotorPersistencia() {
        System.out.println("\n--- Seleccione Motor de Persistencia para Profesiones ---");
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
            keyboard.nextLine(); // consume the invalid input fully
            return -1; // Indicate invalid input to re-prompt
        }
    }
} 