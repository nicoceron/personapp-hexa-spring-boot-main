package co.edu.javeriana.as.personapp.mongo.adapter;

import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument;
import co.edu.javeriana.as.personapp.mongo.mapper.TelefonoMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.TelefonoRepositoryMongo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Adapter("phoneOutputAdapterMongo")
public class PhoneOutputAdapterMongo implements PhoneOutputPort {

    @Autowired
    private TelefonoRepositoryMongo telefonoRepositoryMongo;

    @Autowired
    private TelefonoMapperMongo telefonoMapperMongo;

    @Override
    public Phone save(Phone phone) {
        TelefonoDocument telefonoDocument = telefonoMapperMongo.fromDomainToAdapter(phone);
        TelefonoDocument savedDocument = telefonoRepositoryMongo.save(telefonoDocument);
        return telefonoMapperMongo.fromAdapterToDomain(savedDocument);
    }

    @Override
    public Boolean delete(String number) {
        telefonoRepositoryMongo.deleteById(number);
        return telefonoRepositoryMongo.findById(number).isEmpty();
    }

    @Override
    public List<Phone> find() {
        return telefonoRepositoryMongo.findAll().stream()
                .map(telefonoMapperMongo::fromAdapterToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Phone findById(String number) {
        Optional<TelefonoDocument> optionalTelefonoDocument = telefonoRepositoryMongo.findById(number);
        return optionalTelefonoDocument.map(telefonoMapperMongo::fromAdapterToDomain).orElse(null);
    }

    @Override
    public List<Phone> findByPersonId(Integer personId) {
        // Using the updated findByDuenio method
        List<TelefonoDocument> telefonoDocuments = telefonoRepositoryMongo.findByDuenio(personId);
        return telefonoMapperMongo.fromAdapterListToDomainList(telefonoDocuments);
    }
} 