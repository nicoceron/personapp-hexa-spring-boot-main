package co.edu.javeriana.as.personapp.mongo.adapter;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument;
import co.edu.javeriana.as.personapp.mongo.mapper.ProfesionMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.ProfesionRepositoryMongo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter("professionOutputAdapterMongo") // Bean name for ProfessionUseCase injection
public class ProfessionOutputAdapterMongo implements ProfessionOutputPort {

    @Autowired
    private ProfesionRepositoryMongo profesionRepositoryMongo;

    @Autowired
    private ProfesionMapperMongo profesionMapperMongo;

    @Override
    public Profession save(Profession profession) {
        log.debug("Into save Profession in MongoDB Adapter");
        ProfesionDocument profesionDocument = profesionMapperMongo.fromDomainToAdapter(profession);
        ProfesionDocument persistedProfesion = profesionRepositoryMongo.save(profesionDocument);
        return profesionMapperMongo.fromAdapterToDomain(persistedProfesion);
    }

    @Override
    public Boolean delete(Integer identification) {
        log.debug("Into delete Profession in MongoDB Adapter");
        profesionRepositoryMongo.deleteById(identification);
        return !profesionRepositoryMongo.existsById(identification);
    }

    @Override
    public List<Profession> findAll() {
        log.debug("Into findAll Profession in MongoDB Adapter");
        return profesionRepositoryMongo.findAll().stream()
                .map(profesionMapperMongo::fromAdapterToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Profession findById(Integer identification) {
        log.debug("Into findById Profession in MongoDB Adapter");
        return profesionRepositoryMongo.findById(identification)
                .map(profesionMapperMongo::fromAdapterToDomain)
                .orElse(null);
    }
} 