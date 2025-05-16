package co.edu.javeriana.as.personapp.mariadb.adapter;

import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mariadb.entity.TelefonoEntity;
import co.edu.javeriana.as.personapp.mariadb.mapper.TelefonoMapperMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.TelefonoRepositoryMaria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Adapter("phoneOutputAdapterMaria")
@Transactional
public class PhoneOutputAdapterMaria implements PhoneOutputPort {

    @Autowired
    private TelefonoRepositoryMaria telefonoRepositoryMaria;

    @Autowired
    private TelefonoMapperMaria telefonoMapperMaria;

    @Override
    public Phone save(Phone phone) {
        TelefonoEntity telefonoEntity = telefonoMapperMaria.fromDomainToAdapter(phone);
        TelefonoEntity savedEntity = telefonoRepositoryMaria.save(telefonoEntity);
        return telefonoMapperMaria.fromAdapterToDomain(savedEntity);
    }

    @Override
    public Boolean delete(String number) {
        telefonoRepositoryMaria.deleteById(number);
        return telefonoRepositoryMaria.findById(number).isEmpty();
    }

    @Override
    public List<Phone> find() {
        return telefonoRepositoryMaria.findAll().stream()
                .map(telefonoMapperMaria::fromAdapterToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Phone findById(String number) {
        Optional<TelefonoEntity> optionalTelefonoEntity = telefonoRepositoryMaria.findById(number);
        return optionalTelefonoEntity.map(telefonoMapperMaria::fromAdapterToDomain).orElse(null);
    }

    @Override
    public List<Phone> findByPersonId(Integer personId) {
        List<TelefonoEntity> telefonoEntities = telefonoRepositoryMaria.findByDuenio_Cc(personId);
        return telefonoMapperMaria.fromAdapterListToDomainList(telefonoEntities);
    }
} 