package co.edu.javeriana.as.personapp.application.port.in;

import java.util.List;

import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Phone;

@Port
public interface PhoneInputPort {
    void setPersistence(PhoneOutputPort phoneOutputPort);

    Phone create(Phone phone) throws NoExistException;

    Phone edit(String number, Phone phone) throws NoExistException;

    Boolean drop(String number) throws NoExistException;

    List<Phone> findAll();

    Phone findOne(String number) throws NoExistException;

    Integer count();

    List<Phone> getPhonesOfPerson(Integer personId) throws NoExistException;
} 