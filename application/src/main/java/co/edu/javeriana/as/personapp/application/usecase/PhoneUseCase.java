package co.edu.javeriana.as.personapp.application.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PhoneInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Phone;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
public class PhoneUseCase implements PhoneInputPort {

    private PhoneOutputPort phonePersistence;

    public PhoneUseCase(@Qualifier("phoneOutputAdapterMaria") PhoneOutputPort phonePersistence) {
        this.phonePersistence = phonePersistence;
    }

    @Override
    public void setPersistence(PhoneOutputPort phonePersistence) {
        this.phonePersistence = phonePersistence;
    }

    @Override
    public Phone create(Phone phone) throws NoExistException {
        if (phonePersistence.findById(phone.getNumber()) == null) {
            return phonePersistence.save(phone);
        }
        throw new NoExistException("Cannot create phone, already exists with number: " + phone.getNumber());
    }

    @Override
    public Phone edit(String number, Phone phone) throws NoExistException {
        if (phonePersistence.findById(number) != null) {
            phone.setNumber(number); // Ensure the ID in the object matches the path ID
            return phonePersistence.save(phone);
        }
        throw new NoExistException("Cannot edit phone, does not exist with number: " + number);
    }

    @Override
    public Boolean drop(String number) throws NoExistException {
        if (phonePersistence.findById(number) != null) {
            return phonePersistence.delete(number);
        }
        throw new NoExistException("Cannot drop phone, does not exist with number: " + number);
    }

    @Override
    public List<Phone> findAll() {
        log.info("Listing all phones from persistence");
        return phonePersistence.find();
    }

    @Override
    public Phone findOne(String number) throws NoExistException {
        Phone phone = phonePersistence.findById(number);
        if (phone != null) {
            return phone;
        }
        throw new NoExistException("Phone with number " + number + " does not exist");
    }

    @Override
    public Integer count() {
        return findAll().size();
    }

    @Override
    public List<Phone> getPhonesOfPerson(Integer personId) throws NoExistException {
        // This method assumes the output port has a way to fetch phones by person ID.
        // If not, this logic would need to be findAll().stream().filter(...).collect(...)
        // or the output port would need a new method like findByPersonId(personId).
        // For now, assuming findByPersonId exists on PhoneOutputPort as per previous definition.
        List<Phone> phones = phonePersistence.findByPersonId(personId);
        if (phones == null || phones.isEmpty()) {
            // Depending on requirements, could throw NoExistException or return empty list.
            // Here, returning empty list if no phones, but could also indicate person doesn't exist if that's a separate check.
            log.warn("No phones found for person ID {} or person does not exist.", personId);
        }
        return phones;
    }
} 