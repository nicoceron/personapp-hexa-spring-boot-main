package co.edu.javeriana.as.personapp.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.in.PhoneInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mapper.PhoneMapperRest;
import co.edu.javeriana.as.personapp.model.request.PhoneRequest;
import co.edu.javeriana.as.personapp.model.response.PhoneResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class PhoneInputAdapterRest {

    @Autowired
    @Qualifier("phoneOutputAdapterMaria")
    private PhoneOutputPort phoneOutputPortMaria;

    @Autowired
    @Qualifier("phoneOutputAdapterMongo")
    private PhoneOutputPort phoneOutputPortMongo;
    
    @Autowired
    @Qualifier("personOutputAdapterMaria")
    private PersonOutputPort personOutputPortMaria;

    @Autowired
    @Qualifier("personOutputAdapterMongo")
    private PersonOutputPort personOutputPortMongo;

    @Autowired
    private PhoneInputPort phoneInputPort;
    
    @Autowired
    private PersonInputPort personInputPort;

    @Autowired
    private PhoneMapperRest phoneMapperRest;

    private String setPhoneOutputPortInjection(String dbOption) throws InvalidOptionException {
        if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
            phoneInputPort.setPersistence(phoneOutputPortMaria);
            personInputPort.setPersistence(personOutputPortMaria);
            return DatabaseOption.MARIA.toString();
        } else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
            phoneInputPort.setPersistence(phoneOutputPortMongo);
            personInputPort.setPersistence(personOutputPortMongo);
            return DatabaseOption.MONGO.toString();
        } else {
            throw new InvalidOptionException("Invalid database option: " + dbOption);
        }
    }

    public List<PhoneResponse> findAll(String database) {
        log.info("Into findAll PhoneEntity in Input Adapter");
        try {
            setPhoneOutputPortInjection(database);
            List<Phone> phones = phoneInputPort.findAll();
            return phones.stream()
                    .map(phone -> phoneMapperRest.fromDomainToAdapterRest(phone, database))
                    .collect(Collectors.toList());
        } catch (InvalidOptionException e) {
            log.warn(e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public List<PhoneResponse> findByPersonId(String personId, String database) {
        log.info("Into findByPersonId PhoneEntity in Input Adapter");
        try {
            setPhoneOutputPortInjection(database);
            Integer personIdInt = Integer.parseInt(personId);
            List<Phone> phones = phoneInputPort.getPhonesOfPerson(personIdInt);
            return phones.stream()
                    .map(phone -> phoneMapperRest.fromDomainToAdapterRest(phone, database))
                    .collect(Collectors.toList());
        } catch (InvalidOptionException e) {
            log.warn("Invalid database option: {}", e.getMessage());
            return new ArrayList<>();
        } catch (NoExistException e) {
            log.warn("Person not found: {}", e.getMessage());
            return new ArrayList<>();
        } catch (NumberFormatException e) {
            log.warn("Invalid person ID format: {}", personId);
            return new ArrayList<>();
        }
    }

    public PhoneResponse create(PhoneRequest request) {
        log.info("Into create PhoneEntity in Input Adapter");
        try {
            setPhoneOutputPortInjection(request.getDatabase());
            
            // First, get the person
            Long personId = Long.parseLong(request.getPersonId());
            Person person = personInputPort.findOne(personId);
            
            // Create the phone with the person reference
            Phone phone = phoneMapperRest.fromAdapterToDomain(request, person);
            phone = phoneInputPort.create(phone);
            
            return phoneMapperRest.fromDomainToAdapterRest(phone, request.getDatabase());
        } catch (InvalidOptionException | NoExistException e) {
            log.warn(e.getMessage());
            return new PhoneResponse(request.getNumber(), request.getCompany(), request.getPersonId(), 
                    request.getDatabase(), "ERROR: " + e.getMessage());
        }
    }
    
    public PhoneResponse edit(String number, PhoneRequest request) {
        log.info("Into edit PhoneEntity in Input Adapter");
        try {
            setPhoneOutputPortInjection(request.getDatabase());
            
            // First, get the person
            Long personId = Long.parseLong(request.getPersonId());
            Person person = personInputPort.findOne(personId);
            
            // Create the phone with the person reference
            Phone phone = phoneMapperRest.fromAdapterToDomain(request, person);
            phone = phoneInputPort.edit(number, phone);
            
            return phoneMapperRest.fromDomainToAdapterRest(phone, request.getDatabase());
        } catch (InvalidOptionException | NoExistException e) {
            log.warn(e.getMessage());
            return new PhoneResponse(request.getNumber(), request.getCompany(), request.getPersonId(), 
                    request.getDatabase(), "ERROR: " + e.getMessage());
        }
    }
    
    public PhoneResponse findByNumber(String number, String database) {
        log.info("Into findByNumber PhoneEntity in Input Adapter");
        try {
            setPhoneOutputPortInjection(database);
            Phone phone = phoneInputPort.findOne(number);
            return phoneMapperRest.fromDomainToAdapterRest(phone, database);
        } catch (InvalidOptionException | NoExistException e) {
            log.warn(e.getMessage());
            return phoneMapperRest.createErrorResponse(e.getMessage(), database);
        }
    }
    
    public void delete(String number, String database) {
        log.info("Into delete PhoneEntity in Input Adapter");
        try {
            setPhoneOutputPortInjection(database);
            phoneInputPort.drop(number);
        } catch (InvalidOptionException | NoExistException e) {
            log.warn(e.getMessage());
        }
    }
} 