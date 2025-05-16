package co.edu.javeriana.as.personapp.mariadb.adapter;

import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.mariadb.entity.ProfesionEntity;
import co.edu.javeriana.as.personapp.mariadb.mapper.ProfesionMapperMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.ProfesionRepositoryMaria;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter("professionOutputAdapterMaria") // Bean name for ProfessionUseCase injection
@Transactional
public class ProfessionOutputAdapterMaria implements ProfessionOutputPort {

    @Autowired
    private ProfesionRepositoryMaria profesionRepositoryMaria;

    @Autowired
    private ProfesionMapperMaria profesionMapperMaria;

    @Override
    public Profession save(Profession profession) {
        log.debug("Into save Profession in MariaDB Adapter");
        ProfesionEntity profesionEntity = profesionMapperMaria.fromDomainToAdapter(profession);
        ProfesionEntity persistedProfesion = profesionRepositoryMaria.save(profesionEntity);
        return profesionMapperMaria.fromAdapterToDomain(persistedProfesion);
    }

    @Override
    public Boolean delete(Integer identification) {
        log.debug("Into delete Profession in MariaDB Adapter");
        profesionRepositoryMaria.deleteById(identification);
        return !profesionRepositoryMaria.existsById(identification);
    }

    @Override
    public List<Profession> findAll() {
        log.debug("Into findAll Profession in MariaDB Adapter");
        return profesionRepositoryMaria.findAll().stream()
                .map(profesionMapperMaria::fromAdapterToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Profession findById(Integer identification) {
        log.debug("Into findById Profession in MariaDB Adapter");
        return profesionRepositoryMaria.findById(identification)
                .map(profesionMapperMaria::fromAdapterToDomain)
                .orElse(null);
    }
} 