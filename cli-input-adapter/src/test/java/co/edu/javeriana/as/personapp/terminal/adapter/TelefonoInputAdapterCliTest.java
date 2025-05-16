package co.edu.javeriana.as.personapp.terminal.adapter;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.in.PhoneInputPort;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.terminal.mapper.TelefonoMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TelefonoInputAdapterCliTest {

    @Mock
    private PhoneInputPort phoneInputPort;

    @Mock
    private PersonInputPort personInputPort; // Needed to verify person existence

    @Mock
    private TelefonoMapperCli telefonoMapperCli;

    @InjectMocks
    private TelefonoInputAdapterCli telefonoInputAdapterCli;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final InputStream originalIn = System.in;
    
    // Using System.setOut for output capture in @BeforeEach

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
    void testListarTelefonos_WithData() {
        Person owner = new Person(1, "John", "Doe", Gender.MALE, 30, null, null); // Corrected: Provided Gender.MALE
        Phone phone1Domain = new Phone("1234567", "Claro", owner);
        Phone phone2Domain = new Phone("9876543", "Movistar", owner);
        List<Phone> phonesDomain = List.of(phone1Domain, phone2Domain);

        TelefonoModelCli model1Cli = new TelefonoModelCli("1234567", "Claro", 1, "TEST_DB");
        TelefonoModelCli model2Cli = new TelefonoModelCli("9876543", "Movistar", 1, "TEST_DB");

        when(phoneInputPort.findAll()).thenReturn(phonesDomain);
        when(telefonoMapperCli.fromDomainToAdapterCli(phone1Domain)).thenReturn(model1Cli);
        when(telefonoMapperCli.fromDomainToAdapterCli(phone2Domain)).thenReturn(model2Cli);

        telefonoInputAdapterCli.listarTelefonos();

        String output = outContent.toString();
        assertTrue(output.contains(model1Cli.toString()));
        assertTrue(output.contains(model2Cli.toString()));
        verify(phoneInputPort, times(1)).findAll();
        verify(telefonoMapperCli, times(2)).fromDomainToAdapterCli(any(Phone.class));
    }

    @Test
    void testListarTelefonos_Empty() {
        when(phoneInputPort.findAll()).thenReturn(Collections.emptyList());

        telefonoInputAdapterCli.listarTelefonos();

        String output = outContent.toString();
        assertTrue(output.contains("No phones found."));
        verify(phoneInputPort, times(1)).findAll();
        verify(telefonoMapperCli, never()).fromDomainToAdapterCli(any(Phone.class));
    }

    @Test
    void testListarTelefonos_Exception() {
        String errorMessage = "Database connection error during list";
        when(phoneInputPort.findAll()).thenThrow(new RuntimeException(errorMessage));

        telefonoInputAdapterCli.listarTelefonos();

        String output = outContent.toString();
        assertTrue(output.contains("Error listing phones: " + errorMessage));
        verify(phoneInputPort, times(1)).findAll();
        verify(telefonoMapperCli, never()).fromDomainToAdapterCli(any(Phone.class));
    }

    @Test
    void testObtenerTelefono_Success() throws NoExistException {
        String phoneNumber = "3101234567";
        Scanner scanner = provideInput(phoneNumber + "\n");
        Person owner = new Person(1, "Test", "Owner", Gender.FEMALE, 40, null, null);
        Phone phoneDomain = new Phone(phoneNumber, "Tigo", owner);
        TelefonoModelCli modelCli = new TelefonoModelCli(phoneNumber, "Tigo", owner.getIdentification(), "DB_TEST");

        when(phoneInputPort.findOne(phoneNumber)).thenReturn(phoneDomain);
        when(telefonoMapperCli.fromDomainToAdapterCli(phoneDomain)).thenReturn(modelCli);

        telefonoInputAdapterCli.obtenerTelefono(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Found Phone: " + modelCli.toString()));
        verify(phoneInputPort, times(1)).findOne(phoneNumber);
        verify(telefonoMapperCli, times(1)).fromDomainToAdapterCli(phoneDomain);
    }

    @Test
    void testObtenerTelefono_NotFound_PortThrowsNoExistException() throws NoExistException {
        String phoneNumber = "3209876543";
        Scanner scanner = provideInput(phoneNumber + "\n");
        String exceptionMessage = "Phone with number " + phoneNumber + " does not exist.";

        when(phoneInputPort.findOne(phoneNumber)).thenThrow(new NoExistException(exceptionMessage));

        telefonoInputAdapterCli.obtenerTelefono(scanner);

        String output = outContent.toString();
        assertTrue(output.contains(exceptionMessage));
        verify(phoneInputPort, times(1)).findOne(phoneNumber);
        verify(telefonoMapperCli, never()).fromDomainToAdapterCli(any(Phone.class));
    }

    @Test
    void testObtenerTelefono_GeneralException() throws NoExistException {
        String phoneNumber = "3005550000";
        Scanner scanner = provideInput(phoneNumber + "\n");
        String errorMessage = "Database connection failed while finding phone";

        when(phoneInputPort.findOne(phoneNumber)).thenThrow(new RuntimeException(errorMessage));

        telefonoInputAdapterCli.obtenerTelefono(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Error finding phone: " + errorMessage));
        verify(phoneInputPort, times(1)).findOne(phoneNumber);
        verify(telefonoMapperCli, never()).fromDomainToAdapterCli(any(Phone.class));
    }
    
    @Test
    void testObtenerTelefono_EmptyInput() throws NoExistException {
        String emptyPhoneNumber = "\n"; // Simulates pressing Enter without typing
        Scanner scanner = provideInput(emptyPhoneNumber);
        String exceptionMessage = "Phone with number  does not exist."; // Assuming empty string is passed to port

        // Behavior might depend on how findOne handles empty string. It might throw NoExistException or IllegalArgument.
        // Let's assume it throws NoExistException as per current catch blocks.
        when(phoneInputPort.findOne("")).thenThrow(new NoExistException(exceptionMessage));

        telefonoInputAdapterCli.obtenerTelefono(scanner);

        String output = outContent.toString();
        assertTrue(output.contains(exceptionMessage)); 
        verify(phoneInputPort, times(1)).findOne("");
    }

    @Test
    void testCrearTelefono_Success() throws NoExistException {
        String number = "3112223344";
        String company = "Virgin Mobile";
        int ownerIdInt = 100;
        String input = number + "\n" + company + "\n" + ownerIdInt + "\n";
        Scanner scanner = provideInput(input);

        Person owner = new Person(ownerIdInt, "OwnerName", "OwnerLastName", Gender.OTHER, 50, null, null);
        Phone phoneToCreateDomain = new Phone(number, company, owner);
        // The port's create method returns the created phone

        when(personInputPort.findOne((long)ownerIdInt)).thenReturn(owner);
        when(phoneInputPort.create(any(Phone.class))).thenReturn(phoneToCreateDomain); // Mocking create to return the object

        telefonoInputAdapterCli.crearTelefono(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Phone created successfully."));
        verify(personInputPort, times(1)).findOne((long)ownerIdInt);
        verify(phoneInputPort, times(1)).create(argThat(p -> 
            p.getNumber().equals(number) && 
            p.getCompany().equals(company) && 
            p.getOwner().getIdentification().equals(ownerIdInt)
        ));
    }

    @Test
    void testCrearTelefono_OwnerNotFound() throws NoExistException {
        String number = "3112223355";
        String company = "ETB";
        int ownerIdInt = 101;
        String input = number + "\n" + company + "\n" + ownerIdInt + "\n";
        Scanner scanner = provideInput(input);

        when(personInputPort.findOne((long)ownerIdInt)).thenReturn(null); // Owner does not exist

        telefonoInputAdapterCli.crearTelefono(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Error: Owner with ID " + ownerIdInt + " does not exist."));
        verify(personInputPort, times(1)).findOne((long)ownerIdInt);
        verify(phoneInputPort, never()).create(any(Phone.class));
    }
    
    @Test
    void testCrearTelefono_OwnerNotFound_PortThrowsNoExist() throws NoExistException {
        String number = "3112223355";
        String company = "ETB";
        int ownerIdInt = 101;
        String input = number + "\n" + company + "\n" + ownerIdInt + "\n";
        Scanner scanner = provideInput(input);
        String noExistMsg = "Owner with ID " + ownerIdInt + " does not actually exist in DB.";

        when(personInputPort.findOne((long)ownerIdInt)).thenThrow(new NoExistException(noExistMsg)); 

        telefonoInputAdapterCli.crearTelefono(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Error: " + noExistMsg));
        verify(personInputPort, times(1)).findOne((long)ownerIdInt);
        verify(phoneInputPort, never()).create(any(Phone.class));
    }

    @Test
    void testCrearTelefono_InputMismatch_OwnerId() throws NoExistException {
        String number = "3112223366";
        String company = "Uff";
        String invalidOwnerId = "abc";
        String input = number + "\n" + company + "\n" + invalidOwnerId + "\n123\n"; // Provide valid int after for scanner to recover
        Scanner scanner = provideInput(input);
        
        // This will trigger safeIntegerInput's recovery
        // We need to ensure personInputPort.findOne is called with the recovered ID (123)
        Person recoveredOwner = new Person(123, "Recovered", "Owner", Gender.MALE, 33, null, null);
        when(personInputPort.findOne(123L)).thenReturn(recoveredOwner);
        // And then create should be called
        Phone phoneCreated = new Phone(number, company, recoveredOwner);
        when(phoneInputPort.create(any(Phone.class))).thenReturn(phoneCreated);

        telefonoInputAdapterCli.crearTelefono(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Invalid input. Please enter a number."));
        assertTrue(output.contains("Phone created successfully.")); // Assuming recovery and successful creation
        verify(personInputPort, times(1)).findOne(123L); // Check findOne was called with recovered ID
        verify(phoneInputPort, times(1)).create(any(Phone.class));
    }

    @Test
    void testCrearTelefono_PortThrowsNoExist_PhoneNumberExists() throws NoExistException {
        String number = "3112223377";
        String company = "Avantel";
        int ownerIdInt = 102;
        String input = number + "\n" + company + "\n" + ownerIdInt + "\n";
        Scanner scanner = provideInput(input);
        Person owner = new Person(ownerIdInt, "Some", "Dude", Gender.MALE, 45, null, null);
        String exceptionMessage = "Cannot create phone, already exists with number: " + number;

        when(personInputPort.findOne((long)ownerIdInt)).thenReturn(owner);
        when(phoneInputPort.create(any(Phone.class))).thenThrow(new NoExistException(exceptionMessage));

        telefonoInputAdapterCli.crearTelefono(scanner);

        String output = outContent.toString();
        // The adapter catches NoExistException specifically and prints e.getMessage()
        assertTrue(output.contains("Error: " + exceptionMessage)); 
        verify(phoneInputPort, times(1)).create(any(Phone.class));
    }

    @Test
    void testCrearTelefono_PortThrowsRuntimeException() throws NoExistException {
        String number = "3112223388";
        String company = "Wom";
        int ownerIdInt = 103;
        String input = number + "\n" + company + "\n" + ownerIdInt + "\n";
        Scanner scanner = provideInput(input);
        Person owner = new Person(ownerIdInt, "Another", "Person", Gender.FEMALE, 22, null, null);
        String runtimeExMsg = "General DB Error on create";

        when(personInputPort.findOne((long)ownerIdInt)).thenReturn(owner);
        when(phoneInputPort.create(any(Phone.class))).thenThrow(new RuntimeException(runtimeExMsg));

        telefonoInputAdapterCli.crearTelefono(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Error creating phone: " + runtimeExMsg));
        verify(phoneInputPort, times(1)).create(any(Phone.class));
    }

    @Test
    void testEditarTelefono_Success_ChangeAll() throws NoExistException {
        String number = "3001112233";
        String originalCompany = "OldCom";
        int originalOwnerId = 1;
        Person originalOwner = new Person(originalOwnerId, "Old", "Owner", Gender.MALE, 40, null, null);
        Phone existingPhone = new Phone(number, originalCompany, originalOwner);

        String newCompany = "NewCom";
        int newOwnerId = 2;
        Person newOwner = new Person(newOwnerId, "New", "Owner", Gender.FEMALE, 30, null, null);
        Phone editedPhoneDomain = new Phone(number, newCompany, newOwner);

        String input = number + "\n" + newCompany + "\n" + newOwnerId + "\n";
        Scanner scanner = provideInput(input);

        when(phoneInputPort.findOne(number)).thenReturn(existingPhone);
        when(personInputPort.findOne((long)newOwnerId)).thenReturn(newOwner);
        when(phoneInputPort.edit(eq(number), any(Phone.class))).thenReturn(editedPhoneDomain);

        telefonoInputAdapterCli.editarTelefono(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Phone edited successfully."));
        verify(phoneInputPort, times(1)).findOne(number);
        verify(personInputPort, times(1)).findOne((long)newOwnerId);
        verify(phoneInputPort, times(1)).edit(eq(number), argThat(p -> 
            p.getNumber().equals(number) && 
            p.getCompany().equals(newCompany) && 
            p.getOwner().getIdentification().equals(newOwnerId)
        ));
    }

    @Test
    void testEditarTelefono_Success_KeepCompany() throws NoExistException {
        String number = "3001112244";
        String originalCompany = "KeepCom";
        int originalOwnerId = 3;
        Person originalOwner = new Person(originalOwnerId, "Original", "Owner", Gender.MALE, 40, null, null);
        Phone existingPhone = new Phone(number, originalCompany, originalOwner);

        String newCompanyInput = ""; // Keep original company
        int newOwnerId = 4;
        Person newOwner = new Person(newOwnerId, "Fresh", "Owner", Gender.FEMALE, 30, null, null);
        Phone editedPhoneDomain = new Phone(number, originalCompany, newOwner); // Company remains original

        String input = number + "\n" + newCompanyInput + "\n" + newOwnerId + "\n";
        Scanner scanner = provideInput(input);

        when(phoneInputPort.findOne(number)).thenReturn(existingPhone);
        when(personInputPort.findOne((long)newOwnerId)).thenReturn(newOwner);
        when(phoneInputPort.edit(eq(number), any(Phone.class))).thenReturn(editedPhoneDomain);

        telefonoInputAdapterCli.editarTelefono(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Phone edited successfully."));
        verify(phoneInputPort, times(1)).edit(eq(number), argThat(p -> p.getCompany().equals(originalCompany) && p.getOwner().getIdentification().equals(newOwnerId)));
    }

    @Test
    void testEditarTelefono_Success_KeepOwner() throws NoExistException {
        String number = "3001112255";
        String originalCompany = "OldCompany";
        int originalOwnerId = 5;
        Person originalOwner = new Person(originalOwnerId, "Steady", "Owner", Gender.MALE, 40, null, null);
        Phone existingPhone = new Phone(number, originalCompany, originalOwner);

        String newCompany = "UpdatedCompany";
        String newOwnerIdInput = ""; // Keep original owner
        Phone editedPhoneDomain = new Phone(number, newCompany, originalOwner); // Owner remains original

        String input = number + "\n" + newCompany + "\n" + newOwnerIdInput + "\n";
        Scanner scanner = provideInput(input);

        when(phoneInputPort.findOne(number)).thenReturn(existingPhone);
        // personInputPort.findOne should not be called if ownerIdStr is empty
        when(phoneInputPort.edit(eq(number), any(Phone.class))).thenReturn(editedPhoneDomain);

        telefonoInputAdapterCli.editarTelefono(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Phone edited successfully."));
        verify(personInputPort, never()).findOne(anyLong());
        verify(phoneInputPort, times(1)).edit(eq(number), argThat(p -> p.getCompany().equals(newCompany) && p.getOwner().getIdentification().equals(originalOwnerId)));
    }

    @Test
    void testEditarTelefono_PhoneToEditNotFound() throws NoExistException {
        String number = "3009998877";
        String input = number + "\n"; // Will stop after this if phone not found
        Scanner scanner = provideInput(input);
        String noExistMsg = "Phone with number " + number + " does not exist.";

        when(phoneInputPort.findOne(number)).thenThrow(new NoExistException(noExistMsg));

        telefonoInputAdapterCli.editarTelefono(scanner);

        String output = outContent.toString();
        assertTrue(output.contains(noExistMsg));
        verify(phoneInputPort, times(1)).findOne(number);
        verify(phoneInputPort, never()).edit(anyString(), any(Phone.class));
    }

    @Test
    void testEditarTelefono_NewOwnerNotFound() throws NoExistException {
        String number = "3001112266";
        Phone existingPhone = new Phone(number, "Com", new Person(10, "Old", "Own", Gender.MALE, 20, null, null));
        int newOwnerId = 11;
        String input = number + "\nNewCompany\n" + newOwnerId + "\n";
        Scanner scanner = provideInput(input);

        when(phoneInputPort.findOne(number)).thenReturn(existingPhone);
        when(personInputPort.findOne((long)newOwnerId)).thenReturn(null); // New owner not found

        telefonoInputAdapterCli.editarTelefono(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Error: New Owner with ID " + newOwnerId + " does not exist."));
        verify(phoneInputPort, times(1)).findOne(number);
        verify(personInputPort, times(1)).findOne((long)newOwnerId);
        verify(phoneInputPort, never()).edit(anyString(), any(Phone.class));
    }

    @Test
    void testEditarTelefono_InvalidNewOwnerIdFormat() throws NoExistException {
        String number = "3001112277";
        Phone existingPhone = new Phone(number, "AnyCom", new Person(20, "Current", "Owner", Gender.FEMALE, 33, null, null));
        String invalidOwnerIdInput = "xyz";
        String input = number + "\nNewerCompany\n" + invalidOwnerIdInput + "\n";
        Scanner scanner = provideInput(input);

        when(phoneInputPort.findOne(number)).thenReturn(existingPhone);
        // No need to mock personInputPort.findOne as Integer.parseInt will fail first

        telefonoInputAdapterCli.editarTelefono(scanner);

        String output = outContent.toString();
        // The method catches general Exception, NumberFormatException is one of them.
        assertTrue(output.contains("Error editing phone: For input string: \"" + invalidOwnerIdInput + "\""));
        verify(phoneInputPort, times(1)).findOne(number);
        verify(personInputPort, never()).findOne(anyLong());
        verify(phoneInputPort, never()).edit(anyString(), any(Phone.class));
    }

    @Test
    void testEditarTelefono_PortThrowsNoExistExceptionOnEdit() throws NoExistException {
        String number = "3001112288";
        Person owner = new Person(30, "Some", "Person", Gender.MALE, 50, null, null);
        Phone existingPhone = new Phone(number, "OldCompany", owner);
        String newCompany = "NewCompany";
        String input = number + "\n" + newCompany + "\n\n"; // Keep original owner
        Scanner scanner = provideInput(input);
        String noExistMsg = "Cannot edit phone, does not exist with number: " + number; // From use case

        when(phoneInputPort.findOne(number)).thenReturn(existingPhone);
        when(phoneInputPort.edit(eq(number), any(Phone.class))).thenThrow(new NoExistException(noExistMsg));

        telefonoInputAdapterCli.editarTelefono(scanner);

        String output = outContent.toString();
        assertTrue(output.contains(noExistMsg));
        verify(phoneInputPort, times(1)).edit(eq(number), any(Phone.class));
    }

    @Test
    void testEditarTelefono_PortThrowsRuntimeExceptionOnEdit() throws NoExistException {
        String number = "3001112299";
        Person owner = new Person(40, "Another", "Person", Gender.MALE, 60, null, null);
        Phone existingPhone = new Phone(number, "CompanyX", owner);
        String newCompany = "CompanyY";
        String input = number + "\n" + newCompany + "\n\n"; // Keep original owner
        Scanner scanner = provideInput(input);
        String runtimeExMsg = "DB constraint violation during edit";

        when(phoneInputPort.findOne(number)).thenReturn(existingPhone);
        when(phoneInputPort.edit(eq(number), any(Phone.class))).thenThrow(new RuntimeException(runtimeExMsg));

        telefonoInputAdapterCli.editarTelefono(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Error editing phone: " + runtimeExMsg));
        verify(phoneInputPort, times(1)).edit(eq(number), any(Phone.class));
    }

    @Test
    void testEliminarTelefono_Success() throws NoExistException {
        String phoneNumber = "3151239876";
        Scanner scanner = provideInput(phoneNumber + "\n");

        when(phoneInputPort.drop(phoneNumber)).thenReturn(true); // drop returns Boolean

        telefonoInputAdapterCli.eliminarTelefono(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Phone with number " + phoneNumber + " deleted successfully."));
        verify(phoneInputPort, times(1)).drop(phoneNumber);
    }

    @Test
    void testEliminarTelefono_NotFound_PortThrowsNoExistException() throws NoExistException {
        String phoneNumber = "3169871234";
        Scanner scanner = provideInput(phoneNumber + "\n");
        String exceptionMessage = "Cannot delete phone, does not exist with number: " + phoneNumber;

        when(phoneInputPort.drop(phoneNumber)).thenThrow(new NoExistException(exceptionMessage));

        telefonoInputAdapterCli.eliminarTelefono(scanner);

        String output = outContent.toString();
        assertTrue(output.contains(exceptionMessage));
        verify(phoneInputPort, times(1)).drop(phoneNumber);
    }

    @Test
    void testEliminarTelefono_PortReturnsFalse() throws NoExistException {
        String phoneNumber = "3175554433";
        Scanner scanner = provideInput(phoneNumber + "\n");
        
        // This scenario might occur if drop finds the phone but fails to delete for some other reason (not an exception)
        // and returns false. The current adapter does not specifically handle a false return from drop differently
        // than NoExistException in terms of output message, it just prints the NoExistException message from the catch.
        // The actual PhoneUseCase.drop throws NoExistException if not found, or returns true if delete is successful.
        // It doesn't seem to have a path to return false without throwing NoExistException first if not found.
        // So, testing port returns false might be for an older version or a different port implementation.
        // For current PhoneUseCase, if not found, it throws NoExistException. If found and delete fails (which is rare for boolean ports),
        // it would likely be a RuntimeException from the persistence layer.
        // Let's assume the current use case: drop throws NoExistException if not found or if underlying delete fails to indicate it wasn't found/deleted.
        // Or, for robustness, we can test what happens if for some reason port.drop returns false.
        // The current TelefonoInputAdapterCli's eliminarTelefono only catches NoExistException and general Exception.
        // It does not check the boolean result of phoneInputPort.drop().
        // So, if drop returns false, it will still print "Phone with number ... deleted successfully."
        // This identifies a potential improvement in TelefonoInputAdapterCli.eliminarTelefono to check the boolean.
        // For now, test current behavior:
        when(phoneInputPort.drop(phoneNumber)).thenReturn(false); 

        telefonoInputAdapterCli.eliminarTelefono(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Phone with number " + phoneNumber + " deleted successfully.")); // Current behavior
        verify(phoneInputPort, times(1)).drop(phoneNumber);
    }

    @Test
    void testEliminarTelefono_PortThrowsRuntimeException() throws NoExistException {
        String phoneNumber = "3181234500";
        Scanner scanner = provideInput(phoneNumber + "\n");
        String runtimeExMsg = "DB error during phone deletion";

        when(phoneInputPort.drop(phoneNumber)).thenThrow(new RuntimeException(runtimeExMsg));

        telefonoInputAdapterCli.eliminarTelefono(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Error deleting phone: " + runtimeExMsg));
        verify(phoneInputPort, times(1)).drop(phoneNumber);
    }
    
    @Test
    void testEliminarTelefono_EmptyInput() throws NoExistException {
        String emptyPhoneNumber = "\n"; 
        Scanner scanner = provideInput(emptyPhoneNumber);
        String exceptionMessage = "Cannot delete phone, does not exist with number: "; 

        when(phoneInputPort.drop("")).thenThrow(new NoExistException(exceptionMessage));

        telefonoInputAdapterCli.eliminarTelefono(scanner);

        String output = outContent.toString();
        assertTrue(output.contains(exceptionMessage)); 
        verify(phoneInputPort, times(1)).drop("");
    }

    @Test
    public void testDummy() { // Placeholder to ensure file is runnable
        assertTrue(true);
    }

} 