package co.edu.javeriana.as.personapp.terminal.adapter;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.terminal.mapper.PersonaMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.PersonaModelCli;
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

public class PersonaInputAdapterCliTest {

    @Mock
    private PersonInputPort personInputPort;

    @Mock
    private PersonaMapperCli personaMapperCli;

    @InjectMocks
    private PersonaInputAdapterCli personaInputAdapterCli;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    
    // No DateTimeFormatter needed for Persona as it doesn't directly handle dates in input.

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
    void testContarPersonas_Success() {
        Integer expectedCount = 5;
        when(personInputPort.count()).thenReturn(expectedCount);

        personaInputAdapterCli.contarPersonas();

        String output = outContent.toString();
        assertTrue(output.contains("Número total de personas: " + expectedCount));
        verify(personInputPort, times(1)).count();
    }

    @Test
    void testContarPersonas_Exception() {
        when(personInputPort.count()).thenThrow(new RuntimeException("Database error"));

        personaInputAdapterCli.contarPersonas();

        String output = outContent.toString();
        assertTrue(output.contains("Ocurrió un error inesperado al contar las personas: Database error"));
        verify(personInputPort, times(1)).count();
    }

    @Test
    void testHistorial_WithData() {
        Person person1Domain = new Person(1, "John", "Doe", Gender.MALE, 30, null, null);
        Person person2Domain = new Person(2, "Jane", "Doe", Gender.FEMALE, 28, null, null);
        List<Person> personsDomain = List.of(person1Domain, person2Domain);

        PersonaModelCli model1Cli = new PersonaModelCli(1, "John", "Doe", "MALE", 30);
        PersonaModelCli model2Cli = new PersonaModelCli(2, "Jane", "Doe", "FEMALE", 28);

        when(personInputPort.findAll()).thenReturn(personsDomain);
        when(personaMapperCli.fromDomainToAdapterCli(person1Domain)).thenReturn(model1Cli);
        when(personaMapperCli.fromDomainToAdapterCli(person2Domain)).thenReturn(model2Cli);

        personaInputAdapterCli.historial();

        String output = outContent.toString();
        assertTrue(output.contains(model1Cli.toString()));
        assertTrue(output.contains(model2Cli.toString()));
        verify(personInputPort, times(1)).findAll();
        verify(personaMapperCli, times(2)).fromDomainToAdapterCli(any(Person.class));
    }

    @Test
    void testHistorial_Empty() {
        when(personInputPort.findAll()).thenReturn(Collections.emptyList());

        personaInputAdapterCli.historial();

        String output = outContent.toString();
        // The method prints a log message even when empty.
        // We check that it contains the log message and not much else (e.g., no model string).
        assertTrue(output.contains("Into historial PersonaEntity in Input Adapter"));
        // A more robust check might be to ensure no PersonaModelCli.toString() is present if needed.
        verify(personInputPort, times(1)).findAll();
        verify(personaMapperCli, never()).fromDomainToAdapterCli(any(Person.class));
    }

    @Test
    void testHistorial_Exception() {
        when(personInputPort.findAll()).thenThrow(new RuntimeException("DB connection failed"));

        // The call to personaInputAdapterCli.historial() should be *inside* assertThrows
        assertThrows(RuntimeException.class, () -> {
            personaInputAdapterCli.historial(); 
        });
        // Verify that findAll was indeed called, leading to the exception.
        verify(personInputPort, times(1)).findAll();
    }

    @Test
    void testBuscarPersonaPorId_Success() throws NoExistException {
        long personId = 1L;
        String input = personId + "\n";
        Scanner scanner = provideInput(input);

        Person personDomain = new Person((int) personId, "John", "Doe", Gender.MALE, 30, null, null);
        PersonaModelCli modelCli = new PersonaModelCli((int) personId, "John", "Doe", "MALE", 30);

        when(personInputPort.findOne(personId)).thenReturn(personDomain);
        when(personaMapperCli.fromDomainToAdapterCli(personDomain)).thenReturn(modelCli);

        personaInputAdapterCli.buscarPersonaPorId(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Persona encontrada: " + modelCli.toString()));
        verify(personInputPort, times(1)).findOne(personId);
        verify(personaMapperCli, times(1)).fromDomainToAdapterCli(personDomain);
    }

    @Test
    void testBuscarPersonaPorId_NotFound_PortThrowsNoExistException() throws NoExistException {
        long personId = 2L;
        String input = personId + "\n";
        Scanner scanner = provideInput(input);
        String exceptionMessage = "Persona con CC " + personId + " no existe.";

        when(personInputPort.findOne(personId)).thenThrow(new NoExistException(exceptionMessage));

        personaInputAdapterCli.buscarPersonaPorId(scanner);

        String output = outContent.toString();
        assertTrue(output.contains(exceptionMessage));
        verify(personInputPort, times(1)).findOne(personId);
        verify(personaMapperCli, never()).fromDomainToAdapterCli(any(Person.class));
    }

    @Test
    void testBuscarPersonaPorId_GeneralException() throws NoExistException {
        long personId = 3L;
        String input = personId + "\n";
        Scanner scanner = provideInput(input);
        String errorMessage = "Database connection error";

        when(personInputPort.findOne(personId)).thenThrow(new RuntimeException(errorMessage));

        personaInputAdapterCli.buscarPersonaPorId(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Ocurrió un error inesperado al buscar la persona: " + errorMessage));
        verify(personInputPort, times(1)).findOne(personId);
    }

    @Test
    void testBuscarPersonaPorId_InputMismatch() throws NoExistException {
        String invalidInput = "abc\n";
        Scanner scanner = provideInput(invalidInput);

        personaInputAdapterCli.buscarPersonaPorId(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Error: Tipo de dato incorrecto para el CC. Por favor, intente de nuevo."));
        verify(personInputPort, never()).findOne(anyLong());
    }

    @Test
    void testCrearPersona_Success() {
        int cc = 123;
        String nombre = "TestName";
        String apellido = "TestLastName";
        String generoStr = "MALE";
        int edad = 25;

        String input = cc + "\n" + nombre + "\n" + apellido + "\n" + generoStr + "\n" + edad + "\n";
        Scanner scanner = provideInput(input);

        PersonaModelCli modelCli = new PersonaModelCli(cc, nombre, apellido, generoStr, edad);
        Person personDomainInitial = new Person(cc, nombre, apellido, Gender.valueOf(generoStr), edad, null, null); // Assuming studies and phones are null initially
        Person personDomainCreated = new Person(cc, nombre, apellido, Gender.valueOf(generoStr), edad, Collections.emptyList(), Collections.emptyList()); // Example of a created person

        when(personaMapperCli.fromAdapterToDomain(any(PersonaModelCli.class))).thenReturn(personDomainInitial);
        when(personInputPort.create(personDomainInitial)).thenReturn(personDomainCreated);
        // For the success message, it maps the *created* domain object back to CLI model
        when(personaMapperCli.fromDomainToAdapterCli(personDomainCreated)).thenReturn(modelCli); 

        personaInputAdapterCli.crearPersona(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Persona creada exitosamente: " + modelCli.toString()));
        verify(personInputPort, times(1)).create(personDomainInitial);
        verify(personaMapperCli, times(1)).fromAdapterToDomain(any(PersonaModelCli.class));
        verify(personaMapperCli, times(1)).fromDomainToAdapterCli(personDomainCreated);
    }

    @Test
    void testCrearPersona_InputMismatch_CC() {
        String invalidCc = "abc";
        String input = invalidCc + "\nJohn\nDoe\nMALE\n30\n";
        Scanner scanner = provideInput(input);

        personaInputAdapterCli.crearPersona(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Error: Tipo de dato incorrecto. Por favor, intente de nuevo."));
        verify(personInputPort, never()).create(any(Person.class));
    }

    @Test
    void testCrearPersona_InputMismatch_Age() {
        String input = "123\nJohn\nDoe\nMALE\ninvalidAge\n";
        Scanner scanner = provideInput(input);

        // Mock the mapper for the initial part, as it might be called before age input fails
        // However, the create method reads all inputs first before calling the mapper, so this might not be needed.
        // Let's assume the input mismatch for age happens during scanner.nextInt().

        personaInputAdapterCli.crearPersona(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Error: Tipo de dato incorrecto. Por favor, intente de nuevo."));
        verify(personInputPort, never()).create(any(Person.class));
    }

    @Test
    void testCrearPersona_InvalidGender() {
        int cc = 125;
        String nombre = "TestName";
        String apellido = "TestLastName";
        String invalidGeneroStr = "INVALID_GENDER";
        int edad = 25;
        String input = cc + "\n" + nombre + "\n" + apellido + "\n" + invalidGeneroStr + "\n" + edad + "\n";
        Scanner scanner = provideInput(input);

        // The fromAdapterToDomain in PersonaMapperCli will throw IllegalArgumentException for invalid gender string
        when(personaMapperCli.fromAdapterToDomain(any(PersonaModelCli.class))).thenThrow(new IllegalArgumentException("Invalid gender"));

        personaInputAdapterCli.crearPersona(scanner);

        String output = outContent.toString();
        // The generic catch (Exception e) in crearPersona will catch this.
        assertTrue(output.contains("Ocurrió un error inesperado al crear la persona: Invalid gender"));
        verify(personInputPort, never()).create(any(Person.class));
    }

    @Test
    void testCrearPersona_PortReturnsNull() {
        int cc = 126;
        String input = cc + "\nName\nLastName\nMALE\n33\n";
        Scanner scanner = provideInput(input);
        Person personDomain = new Person(cc, "Name", "LastName", Gender.MALE, 33, null, null);

        when(personaMapperCli.fromAdapterToDomain(any(PersonaModelCli.class))).thenReturn(personDomain);
        when(personInputPort.create(personDomain)).thenReturn(null); // Port fails to create

        personaInputAdapterCli.crearPersona(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Error al crear la persona."));
        verify(personInputPort, times(1)).create(personDomain);
    }

    @Test
    void testCrearPersona_PortThrowsException() {
        int cc = 127;
        String input = cc + "\nName\nLastName\nFEMALE\n40\n";
        Scanner scanner = provideInput(input);
        Person personDomain = new Person(cc, "Name", "LastName", Gender.FEMALE, 40, null, null);
        String exceptionMsg = "Database constraint violation";

        when(personaMapperCli.fromAdapterToDomain(any(PersonaModelCli.class))).thenReturn(personDomain);
        when(personInputPort.create(personDomain)).thenThrow(new RuntimeException(exceptionMsg));

        personaInputAdapterCli.crearPersona(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Ocurrió un error inesperado al crear la persona: " + exceptionMsg));
        verify(personInputPort, times(1)).create(personDomain);
    }

    @Test
    void testEditarPersona_Success_ChangeSomeKeepSome() throws NoExistException {
        long cc = 1L;
        String originalNombre = "OriginalName";
        String originalApellido = "OriginalLastName";
        Gender originalGenero = Gender.MALE;
        int originalEdad = 30;

        String nuevoNombre = "NuevoNombre";
        String nuevoApellido = ""; // Keep original
        String nuevoGeneroStr = "FEMALE";
        String nuevaEdadStr = "35";

        String input = cc + "\n" + nuevoNombre + "\n" + nuevoApellido + "\n" + nuevoGeneroStr + "\n" + nuevaEdadStr + "\n";
        Scanner scanner = provideInput(input);

        Person personToEdit = new Person((int) cc, originalNombre, originalApellido, originalGenero, originalEdad, null, null);
        // Expected domain object that edit receives
        Person personDomainUpdated = new Person((int) cc, nuevoNombre, originalApellido, Gender.valueOf(nuevoGeneroStr), Integer.parseInt(nuevaEdadStr), null, null);
        // Expected CLI model for display after edit
        PersonaModelCli resultModelCli = new PersonaModelCli((int) cc, nuevoNombre, originalApellido, nuevoGeneroStr, Integer.parseInt(nuevaEdadStr));

        when(personInputPort.findOne(cc)).thenReturn(personToEdit);
        when(personaMapperCli.fromDomainToAdapterCli(personToEdit)).thenReturn(new PersonaModelCli((int)cc, originalNombre, originalApellido, originalGenero.toString(), originalEdad)); // For initial display
        when(personaMapperCli.fromAdapterToDomain(any(PersonaModelCli.class))).thenReturn(personDomainUpdated); // Mapper result before calling edit
        when(personInputPort.edit(eq(cc), eq(personDomainUpdated))).thenReturn(personDomainUpdated); // Port returns the updated person
        when(personaMapperCli.fromDomainToAdapterCli(personDomainUpdated)).thenReturn(resultModelCli); // For final display

        personaInputAdapterCli.editarPersona(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Persona actualizada exitosamente: " + resultModelCli.toString()));
        verify(personInputPort, times(1)).findOne(cc);
        verify(personInputPort, times(1)).edit(eq(cc), eq(personDomainUpdated));
    }

    @Test
    void testEditarPersona_NotFound() throws NoExistException {
        long cc = 2L;
        String input = cc + "\n"; // Input will stop if person not found
        Scanner scanner = provideInput(input);
        String exceptionMessage = "Persona con CC " + cc + " no existe para editar.";

        when(personInputPort.findOne(cc)).thenThrow(new NoExistException(exceptionMessage));

        personaInputAdapterCli.editarPersona(scanner);

        String output = outContent.toString();
        assertTrue(output.contains(exceptionMessage));
        verify(personInputPort, times(1)).findOne(cc);
        verify(personInputPort, never()).edit(anyLong(), any(Person.class));
    }

    @Test
    void testEditarPersona_InputMismatch_CC() throws NoExistException {
        String invalidInput = "abc\n";
        Scanner scanner = provideInput(invalidInput);

        personaInputAdapterCli.editarPersona(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Error: Tipo de dato incorrecto para el CC. Por favor, intente de nuevo."));
        verify(personInputPort, never()).findOne(anyLong());
        verify(personInputPort, never()).edit(anyLong(), any(Person.class));
    }
    
    @Test
    void testEditarPersona_InputMismatch_Age_KeepsOld() throws NoExistException {
        long cc = 3L;
        int originalEdad = 25;
        Person personToEdit = new Person((int) cc, "Name", "LastName", Gender.MALE, originalEdad, null, null);
        PersonaModelCli originalModelCli = new PersonaModelCli((int) cc, "Name", "LastName", "MALE", originalEdad);

        String input = cc + "\nNewName\nNewLastName\nFEMALE\ninvalidAge\n"; // Invalid age input
        Scanner scanner = provideInput(input);

        // Person after attempted edit (age should remain originalEdad)
        Person personAfterEditAttempt = new Person((int) cc, "NewName", "NewLastName", Gender.FEMALE, originalEdad, null, null);
        PersonaModelCli modelAfterEditAttempt = new PersonaModelCli((int) cc, "NewName", "NewLastName", "FEMALE", originalEdad);

        when(personInputPort.findOne(cc)).thenReturn(personToEdit);
        when(personaMapperCli.fromDomainToAdapterCli(personToEdit)).thenReturn(originalModelCli); // For initial display
        when(personaMapperCli.fromAdapterToDomain(argThat(m -> m.getEdad() == originalEdad))).thenReturn(personAfterEditAttempt);
        when(personInputPort.edit(eq(cc), eq(personAfterEditAttempt))).thenReturn(personAfterEditAttempt);
        when(personaMapperCli.fromDomainToAdapterCli(personAfterEditAttempt)).thenReturn(modelAfterEditAttempt);

        personaInputAdapterCli.editarPersona(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Edad inválida. Se mantendrá la edad actual."));
        assertTrue(output.contains("Persona actualizada exitosamente: " + modelAfterEditAttempt.toString()));
        verify(personInputPort, times(1)).edit(eq(cc), eq(personAfterEditAttempt));
    }

    @Test
    void testEditarPersona_InvalidGender() throws NoExistException {
        long cc = 4L;
        Person personToEdit = new Person((int) cc, "Name", "LastName", Gender.MALE, 30, null, null);
        String input = cc + "\nNewName\nNewLastName\nINVALID_GENDER\n35\n";
        Scanner scanner = provideInput(input);

        when(personInputPort.findOne(cc)).thenReturn(personToEdit);
        when(personaMapperCli.fromDomainToAdapterCli(personToEdit)).thenReturn(new PersonaModelCli((int)cc, "N", "L", "MALE", 30));
        // This will be thrown by personaMapperCli.fromAdapterToDomain
        when(personaMapperCli.fromAdapterToDomain(any(PersonaModelCli.class))).thenThrow(new IllegalArgumentException("Invalid gender"));

        personaInputAdapterCli.editarPersona(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Ocurrió un error inesperado al editar la persona: Invalid gender"));
        verify(personInputPort, times(1)).findOne(cc);
        verify(personInputPort, never()).edit(anyLong(), any(Person.class));
    }
    
    @Test
    void testEditarPersona_PortReturnsNull() throws NoExistException {
        long cc = 5L;
        Person personToEdit = new Person((int) cc, "N", "L", Gender.MALE, 20, null, null);
        Person editedPersonDomain = new Person((int) cc, "NewN", "NewL", Gender.FEMALE, 21, null, null);
        String input = cc + "\nNewN\nNewL\nFEMALE\n21\n";
        Scanner scanner = provideInput(input);

        when(personInputPort.findOne(cc)).thenReturn(personToEdit);
        when(personaMapperCli.fromDomainToAdapterCli(personToEdit)).thenReturn(new PersonaModelCli((int)cc, "N", "L", "MALE", 20));
        when(personaMapperCli.fromAdapterToDomain(any(PersonaModelCli.class))).thenReturn(editedPersonDomain);
        when(personInputPort.edit(eq(cc), eq(editedPersonDomain))).thenReturn(null); // Port returns null

        personaInputAdapterCli.editarPersona(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Error al actualizar la persona."));
        verify(personInputPort, times(1)).edit(eq(cc), eq(editedPersonDomain));
    }

    @Test
    void testEditarPersona_PortThrowsNoExistExceptionOnEdit() throws NoExistException {
        long cc = 6L;
        Person personToEdit = new Person((int) cc, "N", "L", Gender.MALE, 20, null, null);
        Person editedPersonDomain = new Person((int) cc, "NewN", "NewL", Gender.FEMALE, 21, null, null);
        String input = cc + "\nNewN\nNewL\nFEMALE\n21\n";
        Scanner scanner = provideInput(input);
        String exceptionMessage = "Persona con CC " + cc + " desapareció durante la edición.";

        when(personInputPort.findOne(cc)).thenReturn(personToEdit);
        when(personaMapperCli.fromDomainToAdapterCli(personToEdit)).thenReturn(new PersonaModelCli((int)cc, "N", "L", "MALE", 20));
        when(personaMapperCli.fromAdapterToDomain(any(PersonaModelCli.class))).thenReturn(editedPersonDomain);
        when(personInputPort.edit(eq(cc), eq(editedPersonDomain))).thenThrow(new NoExistException(exceptionMessage));

        personaInputAdapterCli.editarPersona(scanner);

        String output = outContent.toString();
        assertTrue(output.contains(exceptionMessage));
        verify(personInputPort, times(1)).edit(eq(cc), eq(editedPersonDomain));
    }

    @Test
    void testEditarPersona_PortThrowsRuntimeExceptionOnEdit() throws NoExistException {
        long cc = 7L;
        Person personToEdit = new Person((int) cc, "N", "L", Gender.MALE, 20, null, null);
        Person editedPersonDomain = new Person((int) cc, "NewN", "NewL", Gender.FEMALE, 21, null, null);
        String input = cc + "\nNewN\nNewL\nFEMALE\n21\n";
        Scanner scanner = provideInput(input);
        String exceptionMessage = "Database commit failed during edit";

        when(personInputPort.findOne(cc)).thenReturn(personToEdit);
        when(personaMapperCli.fromDomainToAdapterCli(personToEdit)).thenReturn(new PersonaModelCli((int)cc, "N", "L", "MALE", 20));
        when(personaMapperCli.fromAdapterToDomain(any(PersonaModelCli.class))).thenReturn(editedPersonDomain);
        when(personInputPort.edit(eq(cc), eq(editedPersonDomain))).thenThrow(new RuntimeException(exceptionMessage));

        personaInputAdapterCli.editarPersona(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Ocurrió un error inesperado al editar la persona: " + exceptionMessage));
        verify(personInputPort, times(1)).edit(eq(cc), eq(editedPersonDomain));
    }

    @Test
    void testEliminarPersona_Success() throws NoExistException {
        long cc = 1L;
        String input = cc + "\n";
        Scanner scanner = provideInput(input);

        when(personInputPort.drop(cc)).thenReturn(true);

        personaInputAdapterCli.eliminarPersona(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Persona con CC " + cc + " eliminada exitosamente."));
        verify(personInputPort, times(1)).drop(cc);
    }

    @Test
    void testEliminarPersona_NotFound_PortThrowsNoExistException() throws NoExistException {
        long cc = 2L;
        String input = cc + "\n";
        Scanner scanner = provideInput(input);
        String exceptionMessage = "Persona con CC " + cc + " no existe para eliminar.";

        when(personInputPort.drop(cc)).thenThrow(new NoExistException(exceptionMessage));

        personaInputAdapterCli.eliminarPersona(scanner);

        String output = outContent.toString();
        assertTrue(output.contains(exceptionMessage));
        verify(personInputPort, times(1)).drop(cc);
    }

    @Test
    void testEliminarPersona_PortReturnsFalse() throws NoExistException {
        long cc = 3L;
        String input = cc + "\n";
        Scanner scanner = provideInput(input);

        when(personInputPort.drop(cc)).thenReturn(false);

        personaInputAdapterCli.eliminarPersona(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Error al eliminar la persona con CC " + cc + ". Puede que no exista."));
        verify(personInputPort, times(1)).drop(cc);
    }

    @Test
    void testEliminarPersona_InputMismatch_CC() throws NoExistException {
        String invalidInput = "abc\n";
        Scanner scanner = provideInput(invalidInput);

        personaInputAdapterCli.eliminarPersona(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Error: Tipo de dato incorrecto para el CC. Por favor, intente de nuevo."));
        verify(personInputPort, never()).drop(anyLong());
    }

    @Test
    void testEliminarPersona_PortThrowsRuntimeException() throws NoExistException {
        long cc = 4L;
        String input = cc + "\n";
        Scanner scanner = provideInput(input);
        String exceptionMessage = "Database access error during delete";

        when(personInputPort.drop(cc)).thenThrow(new RuntimeException(exceptionMessage));

        personaInputAdapterCli.eliminarPersona(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Ocurrió un error inesperado al eliminar la persona: " + exceptionMessage));
        verify(personInputPort, times(1)).drop(cc);
    }

    // Placeholder to ensure file is runnable
    @Test
    public void testDummy() { 
        assertTrue(true);
    }

} 