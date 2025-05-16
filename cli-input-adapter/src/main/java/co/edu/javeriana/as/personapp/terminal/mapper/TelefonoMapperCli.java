package co.edu.javeriana.as.personapp.terminal.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;

@Mapper
public class TelefonoMapperCli {

    public TelefonoModelCli fromDomainToAdapterCli(Phone phone) {
        TelefonoModelCli telefonoModelCli = new TelefonoModelCli();
        telefonoModelCli.setNumber(phone.getNumber());
        telefonoModelCli.setCompany(phone.getCompany());
        if (phone.getOwner() != null) {
            telefonoModelCli.setOwnerId(phone.getOwner().getIdentification());
        }
        // Database is not part of domain Phone, should be set by adapter if needed
        return telefonoModelCli;
    }

    // This method might not be fully used if creating Phone domain objects
    // directly in adapter based on user input is preferred for CLI.
    // However, providing a basic structure.
    public Phone fromAdapterCliToDomain(TelefonoModelCli telefonoModelCli) {
        Person owner = new Person();
        if (telefonoModelCli.getOwnerId() != null) {
            owner.setIdentification(telefonoModelCli.getOwnerId());
            // Note: For a complete Person object, more details would be needed from user or another source.
            // For CLI create/update, often simpler to build domain object step-by-step in adapter.
        }
        return new Phone(
                telefonoModelCli.getNumber(),
                telefonoModelCli.getCompany(),
                owner // This owner is incomplete, adapter should handle enriching it
        );
    }
} 