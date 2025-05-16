package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.model.request.PhoneRequest;
import co.edu.javeriana.as.personapp.model.response.PhoneResponse;

@Mapper
public class PhoneMapperRest {
    
    public PhoneResponse fromDomainToAdapterRestMaria(Phone phone) {
        return fromDomainToAdapterRest(phone, "MARIA");
    }
    
    public PhoneResponse fromDomainToAdapterRestMongo(Phone phone) {
        return fromDomainToAdapterRest(phone, "MONGO");
    }
    
    public PhoneResponse fromDomainToAdapterRest(Phone phone, String database) {
        return fromDomainToAdapterRest(phone, database, "OK");
    }
    
    public PhoneResponse fromDomainToAdapterRest(Phone phone, String database, String status) {
        return new PhoneResponse(
                phone.getNumber(), 
                phone.getCompany(), 
                String.valueOf(phone.getOwner().getIdentification()),
                database,
                status);
    }

    public Phone fromAdapterToDomain(PhoneRequest request, Person owner) {
        Phone phone = new Phone();
        phone.setNumber(request.getNumber());
        phone.setCompany(request.getCompany());
        phone.setOwner(owner);
        return phone;
    }
    
    public PhoneResponse createErrorResponse(String errorMessage, String database) {
        return new PhoneResponse(
                "", 
                "", 
                "", 
                database,
                "ERROR: " + errorMessage);
    }
} 