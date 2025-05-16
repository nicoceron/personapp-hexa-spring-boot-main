package co.edu.javeriana.as.personapp.terminal.adapter;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.terminal.mapper.StudyMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.StudyModelCli;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class StudyInputAdapterCliTest {

    @Mock
    private StudyInputPort studyInputPort;

    @Mock
    private PersonInputPort personInputPort;

    @Mock
    private ProfessionInputPort professionInputPort;

    @Mock
    private StudyMapperCli studyMapperCli;

    // We don't mock Scanner directly here, but rather provide controlled input streams.
    // Or, we can mock it if finer-grained control over its methods (nextInt, nextLine etc.) is needed per test.
    // For now, let's plan to use controlled InputStreams.

    @InjectMocks
    private StudyInputAdapterCli studyInputAdapterCli;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

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

    // Example of how to reset System.in and System.out if needed after tests, though JUnit usually handles this.
    // @AfterEach
    // public void restoreStreams() {
    //     System.setOut(originalOut);
    //     System.setIn(originalIn);
    // }

    // --- Test Methods Will Go Here ---

    @Test
    public void testCountStudies_Success() {
        // Arrange
        Integer expectedCount = 5;
        when(studyInputPort.count()).thenReturn(expectedCount);
        Scanner mockScanner = provideInput(""); // No input needed for count

        // Act
        studyInputAdapterCli.countStudies(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Counting all Studies..."));
        assertTrue(output.contains("Total number of studies: " + expectedCount));
        verify(studyInputPort, times(1)).count();
    }

    @Test
    public void testCountStudies_Exception() {
        // Arrange
        when(studyInputPort.count()).thenThrow(new RuntimeException("Database error"));
        Scanner mockScanner = provideInput(""); // No input needed for count

        // Act
        studyInputAdapterCli.countStudies(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Counting all Studies..."));
        assertTrue(output.contains("An unexpected error occurred. Please check logs."));
        verify(studyInputPort, times(1)).count();
    }

    @Test
    public void testListAllStudies_WithData() {
        // Arrange
        Person person1 = new Person(1001, "Doe", "John", Gender.MALE, 30, null, null);
        Profession profession1 = new Profession(1, "Engineer", "Desc1", null);
        Study study1 = new Study(person1, profession1, LocalDate.parse("2020-01-01", DATE_FORMATTER), "UnivA");
        
        Person person2 = new Person(1002, "Smith", "Jane", Gender.FEMALE, 28, null, null);
        Profession profession2 = new Profession(2, "Doctor", "Desc2", null);
        Study study2 = new Study(person2, profession2, null, "UnivB");

        List<Study> studies = List.of(study1, study2);
        when(studyInputPort.findAll()).thenReturn(studies);

        StudyModelCli model1 = new StudyModelCli(); // Populate with expected mapped values
        model1.setPersonId(person1.getIdentification());
        model1.setProfessionId(profession1.getIdentification());
        model1.setUniversityName(study1.getUniversityName());
        model1.setGraduationDate(study1.getGraduationDate().format(DATE_FORMATTER));

        StudyModelCli model2 = new StudyModelCli();
        model2.setPersonId(person2.getIdentification());
        model2.setProfessionId(profession2.getIdentification());
        model2.setUniversityName(study2.getUniversityName());
        model2.setGraduationDate(null); // Or however your mapper/model handles null dates for toString
        
        when(studyMapperCli.fromDomainToAdapterCli(study1)).thenReturn(model1);
        when(studyMapperCli.fromDomainToAdapterCli(study2)).thenReturn(model2);

        Scanner mockScanner = provideInput("");

        // Act
        studyInputAdapterCli.listAllStudies(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Listing all Studies..."));
        assertTrue(output.contains("All Studies:"));
        assertTrue(output.contains(model1.toString())); // Relies on StudyModelCli having a decent toString()
        assertTrue(output.contains(model2.toString()));
        verify(studyInputPort, times(1)).findAll();
        verify(studyMapperCli, times(2)).fromDomainToAdapterCli(any(Study.class));
    }

    @Test
    public void testListAllStudies_Empty() {
        // Arrange
        when(studyInputPort.findAll()).thenReturn(Collections.emptyList());
        Scanner mockScanner = provideInput("");

        // Act
        studyInputAdapterCli.listAllStudies(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Listing all Studies..."));
        assertTrue(output.contains("No studies found."));
        verify(studyInputPort, times(1)).findAll();
        verify(studyMapperCli, never()).fromDomainToAdapterCli(any(Study.class));
    }

    @Test
    public void testListAllStudies_Exception() {
        // Arrange
        when(studyInputPort.findAll()).thenThrow(new RuntimeException("Database connection failed"));
        Scanner mockScanner = provideInput("");

        // Act
        studyInputAdapterCli.listAllStudies(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Listing all Studies..."));
        assertTrue(output.contains("An unexpected error occurred. Please check logs."));
        verify(studyInputPort, times(1)).findAll();
    }

    @Test
    public void testFindStudyById_Success() throws NoExistException {
        // Arrange
        Integer personId = 1001;
        Integer professionId = 1;
        String input = personId + "\n" + professionId + "\n";
        Scanner mockScanner = provideInput(input);

        Person person = new Person(personId, "Doe", "John", Gender.MALE, 30, null, null);
        Profession profession = new Profession(professionId, "Engineer", "Desc1", null);
        Study foundStudy = new Study(person, profession, LocalDate.parse("2020-01-01", DATE_FORMATTER), "UnivA");
        StudyModelCli expectedModel = new StudyModelCli(); // Populate as per your mapper
        expectedModel.setPersonId(personId);
        expectedModel.setProfessionId(professionId);
        expectedModel.setUniversityName("UnivA");
        expectedModel.setGraduationDate("2020-01-01");

        when(studyInputPort.findOne(personId, professionId)).thenReturn(foundStudy);
        when(studyMapperCli.fromDomainToAdapterCli(foundStudy)).thenReturn(expectedModel);

        // Act
        studyInputAdapterCli.findStudyById(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Finding Study by ID..."));
        assertTrue(output.contains("Found Study: " + expectedModel.toString()));
        verify(studyInputPort, times(1)).findOne(personId, professionId);
        verify(studyMapperCli, times(1)).fromDomainToAdapterCli(foundStudy);
    }

    @Test
    public void testFindStudyById_NotFound() throws NoExistException {
        // Arrange
        Integer personId = 1001;
        Integer professionId = 1;
        String input = personId + "\n" + professionId + "\n";
        Scanner mockScanner = provideInput(input);

        when(studyInputPort.findOne(personId, professionId)).thenReturn(null);

        // Act
        studyInputAdapterCli.findStudyById(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Finding Study by ID..."));
        assertTrue(output.contains("Study not found for Person ID " + personId + " and Profession ID " + professionId + "."));
        verify(studyInputPort, times(1)).findOne(personId, professionId);
        verify(studyMapperCli, never()).fromDomainToAdapterCli(any(Study.class));
    }

    @Test
    public void testFindStudyById_PortThrowsNoExistException() throws NoExistException {
        // Arrange
        Integer personId = 1002;
        Integer professionId = 2;
        String input = personId + "\n" + professionId + "\n";
        Scanner mockScanner = provideInput(input);
        String exceptionMessage = "Mocked NoExistException from port";

        when(studyInputPort.findOne(personId, professionId)).thenThrow(new NoExistException(exceptionMessage));

        // Act
        studyInputAdapterCli.findStudyById(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Finding Study by ID..."));
        assertTrue(output.contains(exceptionMessage)); // Adapter should print the exception message
        verify(studyInputPort, times(1)).findOne(personId, professionId);
        verify(studyMapperCli, never()).fromDomainToAdapterCli(any(Study.class));
    }

    @Test
    public void testFindStudyById_GeneralException() throws NoExistException {
        // Arrange
        Integer personId = 1003;
        Integer professionId = 3;
        String input = personId + "\n" + professionId + "\n";
        Scanner mockScanner = provideInput(input);

        when(studyInputPort.findOne(personId, professionId)).thenThrow(new RuntimeException("General database error"));

        // Act
        studyInputAdapterCli.findStudyById(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Finding Study by ID..."));
        assertTrue(output.contains("An unexpected error occurred. Please check logs."));
        verify(studyInputPort, times(1)).findOne(personId, professionId);
    }

    @Test
    public void testFindStudyById_InputMismatch_PersonId() throws NoExistException {
        // Arrange
        String input = "abc\n1\n"; // Invalid personId
        Scanner mockScanner = provideInput(input);

        // Act
        studyInputAdapterCli.findStudyById(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Finding Study by ID..."));
        assertTrue(output.contains("Invalid input. Please enter a number."));
        // Ensure we don't call the port if input is bad
        verify(studyInputPort, never()).findOne(anyInt(), anyInt()); 
    }

    @Test
    public void testFindStudyById_InputMismatch_ProfessionId() throws NoExistException {
        // Arrange
        String input = "123\nxyz\n"; // Invalid professionId
        Scanner mockScanner = provideInput(input);

        // Act
        studyInputAdapterCli.findStudyById(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Finding Study by ID..."));
        assertTrue(output.contains("Invalid input. Please enter a number."));
        // Ensure we don't call the port if input is bad
        verify(studyInputPort, never()).findOne(anyInt(), anyInt());
    }

    @Test
    public void testCreateStudy_Success() throws NoExistException {
        // Arrange
        Integer personId = 1001;
        Integer professionId = 1;
        String universityName = "Javeriana Cali";
        String graduationDateStr = "2023-12-01";
        LocalDate graduationDate = LocalDate.parse(graduationDateStr, DATE_FORMATTER);

        String input = personId + "\n" + 
                       professionId + "\n" + 
                       universityName + "\n" + 
                       graduationDateStr + "\n";
        Scanner mockScanner = provideInput(input);

        Person mockPerson = new Person(personId, "Test", "Person", Gender.OTHER, 25, null, null);
        Profession mockProfession = new Profession(professionId, "Test Prof", "Desc", null);
        
        when(personInputPort.findOne(personId.longValue())).thenReturn(mockPerson);
        when(professionInputPort.findOne(professionId)).thenReturn(mockProfession);

        Study studyToCreate = new Study(mockPerson, mockProfession, graduationDate, universityName);
        when(studyInputPort.create(any(Study.class))).thenReturn(studyToCreate); 

        // Act
        studyInputAdapterCli.createStudy(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Creating a new Study..."));
        assertTrue(output.contains("Study created successfully."));
        verify(personInputPort, times(1)).findOne(personId.longValue());
        verify(professionInputPort, times(1)).findOne(professionId);
        verify(studyInputPort, times(1)).create(argThat(study -> 
            study.getPerson().getIdentification().equals(personId) &&
            study.getProfession().getIdentification().equals(professionId) &&
            study.getUniversityName().equals(universityName) &&
            study.getGraduationDate().equals(graduationDate)
        ));
    }

    @Test
    public void testCreateStudy_Success_NoGraduationDate() throws NoExistException {
        // Arrange
        Integer personId = 1002;
        Integer professionId = 2;
        String universityName = "Universidad Nacional";
        String graduationDateStr = ""; // Empty for no date

        String input = personId + "\n" +
                       professionId + "\n" +
                       universityName + "\n" +
                       graduationDateStr + "\n"; // User just presses Enter for date
        Scanner mockScanner = provideInput(input);

        Person mockPerson = new Person(personId, "Another", "User", Gender.FEMALE, 30, null, null);
        Profession mockProfession = new Profession(professionId, "Another Prof", "More Desc", null);

        when(personInputPort.findOne(personId.longValue())).thenReturn(mockPerson);
        when(professionInputPort.findOne(professionId)).thenReturn(mockProfession);

        // Study studyToCreate = new Study(mockPerson, mockProfession, null, universityName); // null for grad date
        // when(studyInputPort.create(any(Study.class))).thenReturn(studyToCreate);
        // More robustly, mock the create to return the object that would be formed
        when(studyInputPort.create(argThat(study -> 
            study.getPerson().getIdentification().equals(personId) &&
            study.getProfession().getIdentification().equals(professionId) &&
            study.getUniversityName().equals(universityName) &&
            study.getGraduationDate() == null
        ))).thenReturn(new Study(mockPerson, mockProfession, null, universityName));


        // Act
        studyInputAdapterCli.createStudy(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Creating a new Study..."));
        assertTrue(output.contains("Study created successfully."));
        verify(personInputPort, times(1)).findOne(personId.longValue());
        verify(professionInputPort, times(1)).findOne(professionId);
        verify(studyInputPort, times(1)).create(argThat(study -> 
            study.getPerson().getIdentification().equals(personId) &&
            study.getProfession().getIdentification().equals(professionId) &&
            study.getUniversityName().equals(universityName) &&
            study.getGraduationDate() == null
        ));
    }

    @Test
    public void testCreateStudy_PersonNotFound() throws NoExistException {
        // Arrange
        Integer personId = 999; // Non-existent person
        Integer professionId = 1;
        String universityName = "Some University";
        String graduationDateStr = "2023-01-01";

        String input = personId + "\n" +
                       professionId + "\n" +
                       universityName + "\n" +
                       graduationDateStr + "\n";
        Scanner mockScanner = provideInput(input);

        when(personInputPort.findOne(personId.longValue())).thenReturn(null); // Person not found
        // No need to mock professionInputPort.findOne if person check fails first

        // Act
        studyInputAdapterCli.createStudy(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Creating a new Study..."));
        assertTrue(output.contains("Error: Person with ID " + personId + " does not exist."));
        verify(personInputPort, times(1)).findOne(personId.longValue());
        verify(professionInputPort, never()).findOne(anyInt());
        verify(studyInputPort, never()).create(any(Study.class));
    }

    @Test
    public void testCreateStudy_ProfessionNotFound() throws NoExistException {
        // Arrange
        Integer personId = 1001;
        Integer professionId = 999; // Non-existent profession
        String universityName = "Another University";
        String graduationDateStr = "2023-02-01";

        String input = personId + "\n" +
                       professionId + "\n" +
                       universityName + "\n" +
                       graduationDateStr + "\n";
        Scanner mockScanner = provideInput(input);

        Person mockPerson = new Person(personId, "Test", "Person", Gender.MALE, 40, null, null);
        when(personInputPort.findOne(personId.longValue())).thenReturn(mockPerson); // Person found
        when(professionInputPort.findOne(professionId)).thenReturn(null); // Profession not found

        // Act
        studyInputAdapterCli.createStudy(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Creating a new Study..."));
        assertTrue(output.contains("Error: Profession with ID " + professionId + " does not exist."));
        verify(personInputPort, times(1)).findOne(personId.longValue());
        verify(professionInputPort, times(1)).findOne(professionId);
        verify(studyInputPort, never()).create(any(Study.class));
    }

    @Test
    public void testCreateStudy_InvalidPersonIdInput_Recovers() throws NoExistException {
        // This test checks if the system prints an error for bad initial Person ID input,
        // but then recovers and successfully creates the study if subsequent input is valid.
        // Arrange
        Integer validPersonIdAfterRecovery = 1;
        Integer professionId = 1;
        String universityName = "Some University";
        String graduationDateStr = "2023-01-01";
        LocalDate graduationDate = LocalDate.parse(graduationDateStr, DATE_FORMATTER);

        String input = "abc\n" +  // Invalid first attempt for Person ID
                       validPersonIdAfterRecovery + "\n" + 
                       professionId + "\n" +
                       universityName + "\n" +
                       graduationDateStr + "\n";
        Scanner mockScanner = provideInput(input);

        Person mockPerson = new Person(validPersonIdAfterRecovery, "Test", "Person", Gender.OTHER, 25, null, null);
        Profession mockProfession = new Profession(professionId, "Test Prof", "Desc", null);
        
        // Mock the calls that will happen AFTER safeIntegerInput recovers
        when(personInputPort.findOne(validPersonIdAfterRecovery.longValue())).thenReturn(mockPerson);
        when(professionInputPort.findOne(professionId)).thenReturn(mockProfession);
        when(studyInputPort.create(any(Study.class))).thenReturn(new Study(mockPerson, mockProfession, graduationDate, universityName));

        // Act
        studyInputAdapterCli.createStudy(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Creating a new Study..."));
        assertTrue(output.contains("Invalid input. Please enter a number.")); // Message for "abc"
        assertTrue(output.contains("Study created successfully.")); // Because it recovered and completed
        
        verify(personInputPort, times(1)).findOne(validPersonIdAfterRecovery.longValue());
        verify(professionInputPort, times(1)).findOne(professionId);
        verify(studyInputPort, times(1)).create(any(Study.class));
    }

    @Test
    public void testCreateStudy_InvalidProfessionIdInput() throws NoExistException {
        // Arrange
        Integer personId = 1001;
        String input = personId + "\n" +
                       "xyz\n" + // Invalid Profession ID
                       "Some University\n" +
                       "2023-01-01\n";
        Scanner mockScanner = provideInput(input);

        Person mockPerson = new Person(personId, "Test", "Person", Gender.OTHER, 25, null, null);
        when(personInputPort.findOne(personId.longValue())).thenReturn(mockPerson); // Person check will pass

        // Act
        studyInputAdapterCli.createStudy(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Creating a new Study..."));
        assertTrue(output.contains("Invalid input. Please enter a number.")); // From safeIntegerInput
        verify(personInputPort, times(1)).findOne(personId.longValue());
        verify(professionInputPort, never()).findOne(anyInt()); // Not called due to bad input for its ID
        verify(studyInputPort, never()).create(any(Study.class));
    }

    @Test
    public void testCreateStudy_InvalidGraduationDateFormat() throws NoExistException {
        // Arrange
        Integer personId = 1001;
        Integer professionId = 1;
        String universityName = "Javeriana Cali";
        String invalidDateStr = "01-12-2023"; // Invalid format

        String input = personId + "\n" +
                       professionId + "\n" +
                       universityName + "\n" +
                       invalidDateStr + "\n";
        Scanner mockScanner = provideInput(input);

        Person mockPerson = new Person(personId, "Test", "Person", Gender.MALE, 30, null, null);
        Profession mockProfession = new Profession(professionId, "Test Prof", "Desc", null);

        when(personInputPort.findOne(personId.longValue())).thenReturn(mockPerson);
        when(professionInputPort.findOne(professionId)).thenReturn(mockProfession);

        // Act
        studyInputAdapterCli.createStudy(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Creating a new Study..."));
        assertTrue(output.contains("Invalid date format. Please use YYYY-MM-DD."));
        verify(personInputPort, times(1)).findOne(personId.longValue());
        verify(professionInputPort, times(1)).findOne(professionId);
        verify(studyInputPort, never()).create(any(Study.class));
    }

    @Test
    public void testCreateStudy_PortReturnsNull() throws NoExistException {
        // Arrange
        Integer personId = 1001;
        Integer professionId = 1;
        String universityName = "Test University";
        String graduationDateStr = "2023-03-03";
        LocalDate graduationDate = LocalDate.parse(graduationDateStr, DATE_FORMATTER);

        String input = personId + "\n" + professionId + "\n" + universityName + "\n" + graduationDateStr + "\n";
        Scanner mockScanner = provideInput(input);

        Person mockPerson = new Person(personId, "Test", "Person", Gender.MALE, 30, null, null);
        Profession mockProfession = new Profession(professionId, "Test Prof", "Desc", null);

        when(personInputPort.findOne(personId.longValue())).thenReturn(mockPerson);
        when(professionInputPort.findOne(professionId)).thenReturn(mockProfession);
        when(studyInputPort.create(any(Study.class))).thenReturn(null); // Simulate creation failure at port

        // Act
        studyInputAdapterCli.createStudy(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Creating a new Study..."));
        assertTrue(output.contains("Failed to create study."));
        verify(studyInputPort, times(1)).create(any(Study.class));
    }

    @Test
    public void testCreateStudy_PortThrowsNoExistExceptionOnCreate() throws NoExistException {
        // Arrange
        Integer personId = 1001;
        Integer professionId = 1;
        String universityName = "Ghost University";
        String graduationDateStr = "2023-04-04";
        String exceptionMessage = "Underlying entity disappeared during create";

        String input = personId + "\n" + professionId + "\n" + universityName + "\n" + graduationDateStr + "\n";
        Scanner mockScanner = provideInput(input);

        Person mockPerson = new Person(personId, "Another", "Person", Gender.FEMALE, 28, null, null);
        Profession mockProfession = new Profession(professionId, "Another", "Prof", null);

        when(personInputPort.findOne(personId.longValue())).thenReturn(mockPerson);
        when(professionInputPort.findOne(professionId)).thenReturn(mockProfession);
        when(studyInputPort.create(any(Study.class))).thenThrow(new NoExistException(exceptionMessage));

        // Act
        studyInputAdapterCli.createStudy(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Creating a new Study..."));
        // The current createStudy method in adapter catches NoExistException generally,
        // so it might print its generic message or the exception's message.
        // Let's assume it prints the exception's message or a related one.
        assertTrue(output.contains(exceptionMessage)); 
        verify(studyInputPort, times(1)).create(any(Study.class));
    }

    @Test
    public void testCreateStudy_PortThrowsRuntimeExceptionOnCreate() throws NoExistException {
        // Arrange
        Integer personId = 1001;
        Integer professionId = 1;
        String universityName = "Error University";
        String graduationDateStr = "2023-05-05";

        String input = personId + "\n" + professionId + "\n" + universityName + "\n" + graduationDateStr + "\n";
        Scanner mockScanner = provideInput(input);

        Person mockPerson = new Person(personId, "Final", "Test", Gender.OTHER, 33, null, null);
        Profession mockProfession = new Profession(professionId, "Final", "Prof", null);

        when(personInputPort.findOne(personId.longValue())).thenReturn(mockPerson);
        when(professionInputPort.findOne(professionId)).thenReturn(mockProfession);
        when(studyInputPort.create(any(Study.class))).thenThrow(new RuntimeException("Database commit failed"));

        // Act
        studyInputAdapterCli.createStudy(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Creating a new Study..."));
        assertTrue(output.contains("An unexpected error occurred. Please check logs."));
        verify(studyInputPort, times(1)).create(any(Study.class));
    }

    @Test
    public void testEditStudy_Success() throws NoExistException {
        // Arrange
        Integer personId = 1001;
        Integer professionId = 1;
        String originalUniversity = "Old University";
        LocalDate originalGradDate = LocalDate.parse("2020-01-01", DATE_FORMATTER);
        String newUniversity = "New University";
        String newGradDateStr = "2022-02-02";
        LocalDate newGradDate = LocalDate.parse(newGradDateStr, DATE_FORMATTER);

        String input = personId + "\n" + 
                       professionId + "\n" + 
                       newUniversity + "\n" + 
                       newGradDateStr + "\n";
        Scanner mockScanner = provideInput(input);

        Person mockPerson = new Person(personId, "Test", "Person", Gender.MALE, 30, null, null);
        Profession mockProfession = new Profession(professionId, "Test Prof", "Desc", null);
        Study existingStudy = new Study(mockPerson, mockProfession, originalGradDate, originalUniversity);
        Study editedStudy = new Study(mockPerson, mockProfession, newGradDate, newUniversity);

        when(studyInputPort.findOne(personId, professionId)).thenReturn(existingStudy);
        when(studyInputPort.edit(eq(personId), eq(professionId), any(Study.class))).thenReturn(editedStudy);

        // Act
        studyInputAdapterCli.editStudy(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Editing a Study..."));
        assertTrue(output.contains("Current University Name: " + originalUniversity));
        assertTrue(output.contains("Current Graduation Date: " + originalGradDate.format(DATE_FORMATTER)));
        assertTrue(output.contains("Study updated successfully."));

        verify(studyInputPort, times(1)).findOne(personId, professionId);
        verify(studyInputPort, times(1)).edit(eq(personId), eq(professionId), argThat(study ->
            study.getUniversityName().equals(newUniversity) &&
            study.getGraduationDate().equals(newGradDate) &&
            study.getPerson().getIdentification().equals(personId) &&
            study.getProfession().getIdentification().equals(professionId)
        ));
    }

    @Test
    public void testEditStudy_KeepExistingValues() throws NoExistException {
        // Arrange
        Integer personId = 1002;
        Integer professionId = 2;
        String originalUniversity = "Original University";
        LocalDate originalGradDate = LocalDate.parse("2021-03-15", DATE_FORMATTER);

        // User presses Enter for new university name and new grad date
        String input = personId + "\n" + 
                       professionId + "\n" + 
                       "\n" + // Keep university
                       "\n";  // Keep grad date
        Scanner mockScanner = provideInput(input);

        Person mockPerson = new Person(personId, "Keeper", "Person", Gender.FEMALE, 35, null, null);
        Profession mockProfession = new Profession(professionId, "Keeper Prof", "Keep Desc", null);
        Study existingStudy = new Study(mockPerson, mockProfession, originalGradDate, originalUniversity);
        // The edited study should be identical to the existing one if values are kept
        
        when(studyInputPort.findOne(personId, professionId)).thenReturn(existingStudy);
        // edit will be called with the original values
        when(studyInputPort.edit(eq(personId), eq(professionId), any(Study.class))).thenReturn(existingStudy);

        // Act
        studyInputAdapterCli.editStudy(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Study updated successfully."));
        verify(studyInputPort, times(1)).edit(eq(personId), eq(professionId), argThat(study ->
            study.getUniversityName().equals(originalUniversity) &&
            study.getGraduationDate().equals(originalGradDate)
        ));
    }

    @Test
    public void testEditStudy_ClearGraduationDate() throws NoExistException {
        // Arrange
        Integer personId = 1003;
        Integer professionId = 3;
        String originalUniversity = "Clearable University";
        LocalDate originalGradDate = LocalDate.parse("2022-06-20", DATE_FORMATTER);
        String newUniversity = "Still Clearable University"; // Can also test keeping this by providing ""

        String input = personId + "\n" + 
                       professionId + "\n" + 
                       newUniversity + "\n" + 
                       "clear\n"; // Clear grad date
        Scanner mockScanner = provideInput(input);

        Person mockPerson = new Person(personId, "Clearer", "Person", Gender.OTHER, 29, null, null);
        Profession mockProfession = new Profession(professionId, "Clearer Prof", "Clear Desc", null);
        Study existingStudy = new Study(mockPerson, mockProfession, originalGradDate, originalUniversity);
        Study studyWithClearedDate = new Study(mockPerson, mockProfession, null, newUniversity);

        when(studyInputPort.findOne(personId, professionId)).thenReturn(existingStudy);
        when(studyInputPort.edit(eq(personId), eq(professionId), any(Study.class))).thenReturn(studyWithClearedDate);

        // Act
        studyInputAdapterCli.editStudy(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Study updated successfully."));
        verify(studyInputPort, times(1)).edit(eq(personId), eq(professionId), argThat(study ->
            study.getUniversityName().equals(newUniversity) &&
            study.getGraduationDate() == null
        ));
    }

    @Test
    public void testEditStudy_NotFound() throws NoExistException {
        // Arrange
        Integer personId = 999; // Non-existent
        Integer professionId = 999;
        String input = personId + "\n" + professionId + "\n"; // Input will stop after IDs if not found
        Scanner mockScanner = provideInput(input);

        when(studyInputPort.findOne(personId, professionId)).thenReturn(null);

        // Act
        studyInputAdapterCli.editStudy(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Editing a Study..."));
        assertTrue(output.contains("Error: Study not found for Person ID " + personId + " and Profession ID " + professionId + "."));
        verify(studyInputPort, times(1)).findOne(personId, professionId);
        verify(studyInputPort, never()).edit(anyInt(), anyInt(), any(Study.class));
    }

    @Test
    public void testEditStudy_InvalidDateFormat() throws NoExistException {
        // Arrange
        Integer personId = 1004;
        Integer professionId = 4;
        String originalUniversity = "DateTest University";
        LocalDate originalGradDate = LocalDate.parse("2019-01-01", DATE_FORMATTER);
        String invalidDateStr = "02-02-2022"; // Invalid format

        String input = personId + "\n" +
                       professionId + "\n" +
                       "Any New University\n" + // University name
                       invalidDateStr + "\n";    // Invalid date
        Scanner mockScanner = provideInput(input);

        Person mockPerson = new Person(personId, "DateTest", "Person", Gender.MALE, 40, null, null);
        Profession mockProfession = new Profession(professionId, "DateTest Prof", "Desc", null);
        Study existingStudy = new Study(mockPerson, mockProfession, originalGradDate, originalUniversity);

        when(studyInputPort.findOne(personId, professionId)).thenReturn(existingStudy);

        // Act
        studyInputAdapterCli.editStudy(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Editing a Study..."));
        assertTrue(output.contains("Invalid date format. Study not updated. Please use YYYY-MM-DD or 'clear'."));
        verify(studyInputPort, times(1)).findOne(personId, professionId);
        verify(studyInputPort, never()).edit(anyInt(), anyInt(), any(Study.class));
    }

    @Test
    public void testEditStudy_PortReturnsNull() throws NoExistException {
        // Arrange
        Integer personId = 1005;
        Integer professionId = 5;
        Study existingStudy = new Study(new Person(personId, "P", "P", Gender.MALE, 1, null, null), new Profession(professionId, "Pr", "D", null), LocalDate.now(), "Univ");
        String input = personId + "\n" + professionId + "\n" + "New Univ\n" + "2023-10-10\n";
        Scanner mockScanner = provideInput(input);

        when(studyInputPort.findOne(personId, professionId)).thenReturn(existingStudy);
        when(studyInputPort.edit(eq(personId), eq(professionId), any(Study.class))).thenReturn(null);

        // Act
        studyInputAdapterCli.editStudy(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Failed to update study."));
        verify(studyInputPort, times(1)).edit(eq(personId), eq(professionId), any(Study.class));
    }

    @Test
    public void testEditStudy_PortThrowsNoExistExceptionOnEdit() throws NoExistException {
        // Arrange
        Integer personId = 1006;
        Integer professionId = 6;
        String exceptionMessage = "Underlying entity disappeared during edit";
        Study existingStudy = new Study(new Person(personId, "P", "P", Gender.FEMALE, 1, null, null), new Profession(professionId, "Pr", "D", null), LocalDate.now(), "Univ");
        String input = personId + "\n" + professionId + "\n" + "New Univ\n" + "2023-10-10\n";
        Scanner mockScanner = provideInput(input);

        when(studyInputPort.findOne(personId, professionId)).thenReturn(existingStudy);
        when(studyInputPort.edit(eq(personId), eq(professionId), any(Study.class))).thenThrow(new NoExistException(exceptionMessage));

        // Act
        studyInputAdapterCli.editStudy(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains(exceptionMessage));
        verify(studyInputPort, times(1)).edit(eq(personId), eq(professionId), any(Study.class));
    }

    @Test
    public void testEditStudy_PortThrowsRuntimeExceptionOnEdit() throws NoExistException {
        // Arrange
        Integer personId = 1007;
        Integer professionId = 7;
        Study existingStudy = new Study(new Person(personId, "P", "P", Gender.OTHER, 1, null, null), new Profession(professionId, "Pr", "D", null), LocalDate.now(), "Univ");
        String input = personId + "\n" + professionId + "\n" + "New Univ\n" + "2023-10-10\n";
        Scanner mockScanner = provideInput(input);

        when(studyInputPort.findOne(personId, professionId)).thenReturn(existingStudy);
        when(studyInputPort.edit(eq(personId), eq(professionId), any(Study.class))).thenThrow(new RuntimeException("DB error during edit"));

        // Act
        studyInputAdapterCli.editStudy(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("An unexpected error occurred. Please check logs."));
        verify(studyInputPort, times(1)).edit(eq(personId), eq(professionId), any(Study.class));
    }
    
    @Test
    public void testEditStudy_InputMismatch_PersonId() throws NoExistException {
        // Arrange
        Integer validPersonId = 1;
        Integer validProfessionId = 1; // Provide a valid profession ID for recovery path
        String newUniversityName = "New Name";
        String newGradDate = "2023-01-01";

        String input = "abc\n" +  // Invalid first attempt for Person ID
                       validPersonId + "\n" + 
                       validProfessionId + "\n" + 
                       newUniversityName + "\n" + 
                       newGradDate + "\n"; 
        Scanner mockScanner = provideInput(input);

        Person mockPerson = new Person(validPersonId, "P", "P", Gender.MALE, 1, null, null);
        // Profession also needs to be mocked for the validProfessionId
        Profession mockProfession = new Profession(validProfessionId, "Pf", "D", null);
        Study mockStudy = new Study(mockPerson, mockProfession, LocalDate.now().minusYears(1), "Old University"); // Existing study
        Study editedStudy = new Study(mockPerson, mockProfession, LocalDate.parse(newGradDate, DATE_FORMATTER), newUniversityName);

        // This will be called after safeIntegerInput recovers for personId
        // when(personInputPort.findOne(validPersonId.longValue())).thenReturn(mockPerson); // Not needed as editStudy doesn't use personInputPort
        when(studyInputPort.findOne(validPersonId, validProfessionId)).thenReturn(mockStudy);
        when(studyInputPort.edit(eq(validPersonId), eq(validProfessionId), any(Study.class))).thenReturn(editedStudy);

        // Act
        studyInputAdapterCli.editStudy(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Invalid input. Please enter a number.")); // For "abc"
        assertTrue(output.contains("Study updated successfully.")); // Because it recovered for both IDs
        verify(studyInputPort,times(1)).findOne(validPersonId, validProfessionId);
        verify(studyInputPort,times(1)).edit(eq(validPersonId), eq(validProfessionId), any(Study.class));
    }

    @Test
    public void testEditStudy_InputMismatch_ProfessionId() throws NoExistException {
        // Arrange
        Integer personId = 1;
        String input = personId + "\nxyz\n1\nNew Name\n2023-01-01\n"; 
        Scanner mockScanner = provideInput(input);
        Study existingStudy = new Study(new Person(personId, "P", "P", Gender.MALE, 1, null, null), new Profession(1, "Pr", "D", null), LocalDate.now(), "Univ");
        
        when(studyInputPort.findOne(eq(personId), eq(1))).thenReturn(existingStudy);
        when(studyInputPort.edit(eq(personId), eq(1), any(Study.class))).thenReturn(existingStudy);

        // Act
        studyInputAdapterCli.editStudy(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Invalid input. Please enter a number.")); // For the prof id
        // Since safeIntegerInput recovers, it will use '1' for professionId
        assertTrue(output.contains("Study updated successfully.")); 
        verify(studyInputPort, times(1)).findOne(eq(personId), eq(1));
        verify(studyInputPort, times(1)).edit(eq(personId), eq(1), any(Study.class));
    }

    @Test
    public void testDeleteStudy_Success() throws NoExistException {
        // Arrange
        Integer personId = 1001;
        Integer professionId = 1;
        String input = personId + "\n" + 
                       professionId + "\n" + 
                       "yes\n"; // Confirm deletion
        Scanner mockScanner = provideInput(input);

        when(studyInputPort.drop(personId, professionId)).thenReturn(true);

        // Act
        studyInputAdapterCli.deleteStudy(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Deleting a Study..."));
        assertTrue(output.contains("Are you sure you want to delete the study for Person ID " + personId + " and Profession ID " + professionId + "? (yes/no)"));
        assertTrue(output.contains("Study deleted successfully."));
        verify(studyInputPort, times(1)).drop(personId, professionId);
    }

    @Test
    public void testDeleteStudy_UserCancels_No() throws NoExistException {
        // Arrange
        Integer personId = 1002;
        Integer professionId = 2;
        String input = personId + "\n" + 
                       professionId + "\n" + 
                       "no\n"; // Cancel deletion
        Scanner mockScanner = provideInput(input);

        // Act
        studyInputAdapterCli.deleteStudy(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Deleting a Study..."));
        assertTrue(output.contains("Study deletion cancelled."));
        verify(studyInputPort, never()).drop(anyInt(), anyInt());
    }

    @Test
    public void testDeleteStudy_UserCancels_OtherInput() throws NoExistException {
        // Arrange
        Integer personId = 1003;
        Integer professionId = 3;
        String input = personId + "\n" + 
                       professionId + "\n" + 
                       "maybe\n"; // Neither yes nor no
        Scanner mockScanner = provideInput(input);

        // Act
        studyInputAdapterCli.deleteStudy(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Deleting a Study..."));
        assertTrue(output.contains("Study deletion cancelled."));
        verify(studyInputPort, never()).drop(anyInt(), anyInt());
    }

    @Test
    public void testDeleteStudy_PortReturnsFalse() throws NoExistException {
        // Arrange
        Integer personId = 1004;
        Integer professionId = 4;
        String input = personId + "\n" + professionId + "\n" + "yes\n";
        Scanner mockScanner = provideInput(input);

        when(studyInputPort.drop(personId, professionId)).thenReturn(false); // Simulate failure at port

        // Act
        studyInputAdapterCli.deleteStudy(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Deleting a Study..."));
        assertTrue(output.contains("Failed to delete study. It might not exist or an error occurred."));
        verify(studyInputPort, times(1)).drop(personId, professionId);
    }

    @Test
    public void testDeleteStudy_PortThrowsNoExistException() throws NoExistException {
        // Arrange
        Integer personId = 1005;
        Integer professionId = 5;
        String exceptionMessage = "Cannot delete, study does not exist";
        String input = personId + "\n" + professionId + "\n" + "yes\n";
        Scanner mockScanner = provideInput(input);

        when(studyInputPort.drop(personId, professionId)).thenThrow(new NoExistException(exceptionMessage));

        // Act
        studyInputAdapterCli.deleteStudy(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Deleting a Study..."));
        assertTrue(output.contains(exceptionMessage)); // Adapter should print the specific message
        verify(studyInputPort, times(1)).drop(personId, professionId);
    }

    @Test
    public void testDeleteStudy_PortThrowsRuntimeException() throws NoExistException {
        // Arrange
        Integer personId = 1006;
        Integer professionId = 6;
        String input = personId + "\n" + professionId + "\n" + "yes\n";
        Scanner mockScanner = provideInput(input);

        when(studyInputPort.drop(personId, professionId)).thenThrow(new RuntimeException("Database access error during delete"));

        // Act
        studyInputAdapterCli.deleteStudy(mockScanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Deleting a Study..."));
        assertTrue(output.contains("An unexpected error occurred. Please check logs."));
        verify(studyInputPort, times(1)).drop(personId, professionId);
    }

    @Test
    void testDeleteStudy_InputMismatch_PersonId_Recovers() throws NoExistException {
        String simulatedInput = "invalid\n1\n1\nyes\n"; // Invalid personId, then valid, then professionId, then confirm
        Scanner localScanner = provideInput(simulatedInput);

        when(studyInputPort.drop(1, 1)).thenReturn(true);

        studyInputAdapterCli.deleteStudy(localScanner);

        String output = outContent.toString();
        assertTrue(output.contains("Invalid input. Please enter a number."));
        assertTrue(output.contains("Study deleted successfully."));
        verify(studyInputPort).drop(1, 1);
    }

    @Test
    void testDeleteStudy_InputMismatch_ProfessionId_Recovers() throws NoExistException {
        String simulatedInput = "1\ninvalid\n1\nyes\n"; // Valid personId, then invalid professionId, then valid, then confirm
        Scanner localScanner = provideInput(simulatedInput);

        when(studyInputPort.drop(1, 1)).thenReturn(true);

        studyInputAdapterCli.deleteStudy(localScanner);

        String output = outContent.toString();
        assertTrue(output.contains("Invalid input. Please enter a number."));
        assertTrue(output.contains("Study deleted successfully."));
        verify(studyInputPort).drop(1, 1);
    }

    @Test
    public void testDummy() {
        assertTrue(true); // Placeholder
    }

} 