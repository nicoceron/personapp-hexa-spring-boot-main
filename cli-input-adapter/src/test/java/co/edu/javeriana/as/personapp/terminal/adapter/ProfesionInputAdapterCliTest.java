package co.edu.javeriana.as.personapp.terminal.adapter;

import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.terminal.mapper.ProfesionMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.ProfesionModelCli;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class ProfesionInputAdapterCliTest {

    @Mock
    private ProfessionInputPort professionInputPort;

    @Mock
    private ProfesionMapperCli profesionMapperCli;

    @InjectMocks
    private ProfesionInputAdapterCli profesionInputAdapterCli;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setOut(new PrintStream(outContent)); // Capture System.out
    }

    private Scanner provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
        return new Scanner(testIn);
    }

    // --- Test Methods Will Go Here ---

    @Test
    void testContarProfesiones_Success() {
        Integer expectedCount = 3;
        when(professionInputPort.count()).thenReturn(expectedCount);

        profesionInputAdapterCli.contarProfesiones();

        String output = outContent.toString();
        assertTrue(output.contains("Total professions: " + expectedCount));
        verify(professionInputPort, times(1)).count();
    }

    @Test
    void testContarProfesiones_Exception() {
        String errorMessage = "Database connection failed";
        when(professionInputPort.count()).thenThrow(new RuntimeException(errorMessage));

        profesionInputAdapterCli.contarProfesiones();

        String output = outContent.toString();
        assertTrue(output.contains("Error counting professions: " + errorMessage));
        verify(professionInputPort, times(1)).count();
    }

    @Test
    void testListarProfesiones_WithData() {
        Profession prof1Domain = new Profession(1, "Ingeniero", "Ingenieria de Sistemas", null);
        Profession prof2Domain = new Profession(2, "Medico", "Medicina General", null);
        List<Profession> professionsDomain = List.of(prof1Domain, prof2Domain);

        ProfesionModelCli model1Cli = new ProfesionModelCli(1, "Ingeniero", "Ingenieria de Sistemas");
        ProfesionModelCli model2Cli = new ProfesionModelCli(2, "Medico", "Medicina General");

        when(professionInputPort.findAll()).thenReturn(professionsDomain);
        when(profesionMapperCli.fromDomainToAdapterCli(prof1Domain)).thenReturn(model1Cli);
        when(profesionMapperCli.fromDomainToAdapterCli(prof2Domain)).thenReturn(model2Cli);

        profesionInputAdapterCli.listarProfesiones();

        String output = outContent.toString();
        assertTrue(output.contains(model1Cli.toString()));
        assertTrue(output.contains(model2Cli.toString()));
        verify(professionInputPort, times(1)).findAll();
        verify(profesionMapperCli, times(2)).fromDomainToAdapterCli(any(Profession.class));
    }

    @Test
    void testListarProfesiones_Empty() {
        when(professionInputPort.findAll()).thenReturn(Collections.emptyList());

        profesionInputAdapterCli.listarProfesiones();

        String output = outContent.toString();
        assertTrue(output.contains("No professions found."));
        verify(professionInputPort, times(1)).findAll();
        verify(profesionMapperCli, never()).fromDomainToAdapterCli(any(Profession.class));
    }

    @Test
    void testListarProfesiones_Exception() {
        String errorMessage = "Error accessing database";
        when(professionInputPort.findAll()).thenThrow(new RuntimeException(errorMessage));

        profesionInputAdapterCli.listarProfesiones();

        String output = outContent.toString();
        assertTrue(output.contains("Error listing professions: " + errorMessage));
        verify(professionInputPort, times(1)).findAll();
        verify(profesionMapperCli, never()).fromDomainToAdapterCli(any(Profession.class)); // Mapper not called if findAll fails
    }

    @Test
    void testObtenerProfesion_Success() throws NoExistException {
        Integer id = 1;
        Profession professionDomain = new Profession(id, "Abogado", "Derecho Penal", null);
        ProfesionModelCli modelCli = new ProfesionModelCli(id, "Abogado", "Derecho Penal");

        when(professionInputPort.findOne(id)).thenReturn(professionDomain);
        when(profesionMapperCli.fromDomainToAdapterCli(professionDomain)).thenReturn(modelCli);

        profesionInputAdapterCli.obtenerProfesion(id);

        String output = outContent.toString();
        assertTrue(output.contains("Found Profession: " + modelCli.toString()));
        verify(professionInputPort, times(1)).findOne(id);
        verify(profesionMapperCli, times(1)).fromDomainToAdapterCli(professionDomain);
    }

    @Test
    void testObtenerProfesion_NotFound_PortThrowsNoExistException() throws NoExistException {
        Integer id = 2;
        String exceptionMessage = "Profession with ID " + id + " does not exist.";
        when(professionInputPort.findOne(id)).thenThrow(new NoExistException(exceptionMessage));

        profesionInputAdapterCli.obtenerProfesion(id);

        String output = outContent.toString();
        assertTrue(output.contains(exceptionMessage));
        verify(professionInputPort, times(1)).findOne(id);
        verify(profesionMapperCli, never()).fromDomainToAdapterCli(any(Profession.class));
    }

    @Test
    void testObtenerProfesion_GeneralException() throws NoExistException {
        Integer id = 3;
        String errorMessage = "General database error";
        when(professionInputPort.findOne(id)).thenThrow(new RuntimeException(errorMessage));

        profesionInputAdapterCli.obtenerProfesion(id);

        String output = outContent.toString();
        assertTrue(output.contains("Error finding profession: " + errorMessage));
        verify(professionInputPort, times(1)).findOne(id);
        verify(profesionMapperCli, never()).fromDomainToAdapterCli(any(Profession.class));
    }

    @Test
    void testCrearProfesion_Success() throws NoExistException {
        ProfesionModelCli modelCli = new ProfesionModelCli(10, "Arquitecto", "Diseño de Edificios");
        Profession professionDomain = new Profession(10, "Arquitecto", "Diseño de Edificios", null);

        when(profesionMapperCli.fromAdapterCliToDomain(modelCli)).thenReturn(professionDomain);
        // For create, usually it might return the created object or void/boolean.
        // The ProfesionUseCase.create returns the created Profession object.
        when(professionInputPort.create(professionDomain)).thenReturn(professionDomain); 

        profesionInputAdapterCli.crearProfesion(modelCli);

        String output = outContent.toString();
        assertTrue(output.contains("Profession created successfully."));
        verify(profesionMapperCli, times(1)).fromAdapterCliToDomain(modelCli);
        verify(professionInputPort, times(1)).create(professionDomain);
    }

    @Test
    void testCrearProfesion_MapperException() {
        ProfesionModelCli modelCli = new ProfesionModelCli(11, "Analista", null); // e.g. null description might be invalid
        String mapperErrorMessage = "Invalid profession data in model";
        when(profesionMapperCli.fromAdapterCliToDomain(modelCli)).thenThrow(new IllegalArgumentException(mapperErrorMessage));

        profesionInputAdapterCli.crearProfesion(modelCli);

        String output = outContent.toString();
        assertTrue(output.contains("Error creating profession: " + mapperErrorMessage));
        verify(profesionMapperCli, times(1)).fromAdapterCliToDomain(modelCli);
        verify(professionInputPort, never()).create(any(Profession.class));
    }

    @Test
    void testCrearProfesion_PortThrowsRuntimeException() throws NoExistException {
        ProfesionModelCli modelCli = new ProfesionModelCli(13, "Manager", "Project Management");
        Profession professionDomain = new Profession(13, "Manager", "Project Management", null);
        String runtimeErrorMessage = "Database constraint violation on create";

        when(profesionMapperCli.fromAdapterCliToDomain(modelCli)).thenReturn(professionDomain);
        when(professionInputPort.create(professionDomain)).thenThrow(new RuntimeException(runtimeErrorMessage));

        profesionInputAdapterCli.crearProfesion(modelCli);

        String output = outContent.toString();
        assertTrue(output.contains("Error creating profession: " + runtimeErrorMessage));
        verify(professionInputPort, times(1)).create(professionDomain);
    }

    @Test
    void testEditarProfesion_Success() throws NoExistException {
        Integer id = 1;
        ProfesionModelCli modelCliToEdit = new ProfesionModelCli(id, "Ingeniero de Software", "Desarrollo de Software");
        Profession professionDomainMapped = new Profession(id, "Ingeniero de Software", "Desarrollo de Software", null);
        // Assume edit in port returns the edited object or the same object reference
        when(profesionMapperCli.fromAdapterCliToDomain(modelCliToEdit)).thenReturn(professionDomainMapped);
        when(professionInputPort.edit(id, professionDomainMapped)).thenReturn(professionDomainMapped);

        profesionInputAdapterCli.editarProfesion(modelCliToEdit);

        String output = outContent.toString();
        assertTrue(output.contains("Profession edited successfully."));
        verify(profesionMapperCli, times(1)).fromAdapterCliToDomain(modelCliToEdit);
        verify(professionInputPort, times(1)).edit(id, professionDomainMapped);
    }

    @Test
    void testEditarProfesion_MapperException() throws NoExistException {
        ProfesionModelCli modelCliToEdit = new ProfesionModelCli(2, "Analista de Datos", null); // e.g. Invalid model
        String mapperErrorMessage = "Invalid data for profession edit";
        when(profesionMapperCli.fromAdapterCliToDomain(modelCliToEdit)).thenThrow(new IllegalArgumentException(mapperErrorMessage));

        profesionInputAdapterCli.editarProfesion(modelCliToEdit);

        String output = outContent.toString();
        assertTrue(output.contains("Error editing profession: " + mapperErrorMessage));
        verify(profesionMapperCli, times(1)).fromAdapterCliToDomain(modelCliToEdit);
        verify(professionInputPort, never()).edit(anyInt(), any(Profession.class));
    }

    @Test
    void testEditarProfesion_PortThrowsNoExistException() throws NoExistException {
        Integer id = 3;
        ProfesionModelCli modelCliToEdit = new ProfesionModelCli(id, "Cientifico", "Investigacion");
        Profession professionDomainMapped = new Profession(id, "Cientifico", "Investigacion", null);
        String noExistMessage = "Profession with ID " + id + " not found for editing.";

        when(profesionMapperCli.fromAdapterCliToDomain(modelCliToEdit)).thenReturn(professionDomainMapped);
        when(professionInputPort.edit(id, professionDomainMapped)).thenThrow(new NoExistException(noExistMessage));

        profesionInputAdapterCli.editarProfesion(modelCliToEdit);

        String output = outContent.toString();
        assertTrue(output.contains(noExistMessage)); // The adapter prints the exception message directly
        verify(professionInputPort, times(1)).edit(id, professionDomainMapped);
    }

    @Test
    void testEditarProfesion_PortThrowsRuntimeException() throws NoExistException {
        Integer id = 4;
        ProfesionModelCli modelCliToEdit = new ProfesionModelCli(id, "Diseñador", "Diseño Grafico");
        Profession professionDomainMapped = new Profession(id, "Diseñador", "Diseño Grafico", null);
        String runtimeErrorMessage = "Database error during edit operation";

        when(profesionMapperCli.fromAdapterCliToDomain(modelCliToEdit)).thenReturn(professionDomainMapped);
        when(professionInputPort.edit(id, professionDomainMapped)).thenThrow(new RuntimeException(runtimeErrorMessage));

        profesionInputAdapterCli.editarProfesion(modelCliToEdit);

        String output = outContent.toString();
        assertTrue(output.contains("Error editing profession: " + runtimeErrorMessage));
        verify(professionInputPort, times(1)).edit(id, professionDomainMapped);
    }

    @Test
    void testEliminarProfesion_Success() throws NoExistException {
        Integer id = 1;
        // Mocking the behavior of drop returning true (successful deletion)
        when(professionInputPort.drop(id)).thenReturn(true); // drop in ProfessionInputPort returns Boolean

        profesionInputAdapterCli.eliminarProfesion(id);

        String output = outContent.toString();
        assertTrue(output.contains("Profession with ID " + id + " deleted successfully."));
        verify(professionInputPort, times(1)).drop(id);
    }

    @Test
    void testEliminarProfesion_PortThrowsNoExistException() throws NoExistException {
        Integer id = 2;
        String noExistMessage = "Profession with ID " + id + " not found for deletion.";
        when(professionInputPort.drop(id)).thenThrow(new NoExistException(noExistMessage));

        profesionInputAdapterCli.eliminarProfesion(id);

        String output = outContent.toString();
        assertTrue(output.contains(noExistMessage)); // Adapter prints the exception message
        verify(professionInputPort, times(1)).drop(id);
    }

    @Test
    void testEliminarProfesion_PortThrowsRuntimeException() throws NoExistException {
        Integer id = 3;
        String runtimeErrorMessage = "Database error during delete operation";
        when(professionInputPort.drop(id)).thenThrow(new RuntimeException(runtimeErrorMessage));

        profesionInputAdapterCli.eliminarProfesion(id);

        String output = outContent.toString();
        assertTrue(output.contains("Error deleting profession: " + runtimeErrorMessage));
        verify(professionInputPort, times(1)).drop(id);
    }

    @Test
    public void testDummy() { // Placeholder to ensure file is runnable
        assertTrue(true);
    }

} 