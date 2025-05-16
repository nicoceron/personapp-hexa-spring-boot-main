package co.edu.javeriana.as.personapp.mongo.adapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.mapper.EstudiosMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.EstudiosRepositoryMongo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter("studyOutputAdapterMongo")
public class StudyOutputAdapterMongo implements StudyOutputPort {

    @Autowired
    private EstudiosRepositoryMongo estudiosRepositoryMongo;

    @Autowired
    private EstudiosMapperMongo estudiosMapperMongo;

    @Override
    public Study save(Study study) {
        log.debug("Into save StudyEntity in Mongo DB");
        EstudiosDocument estudiosDocument = estudiosMapperMongo.fromDomainToAdapter(study);
        EstudiosDocument savedDocument = estudiosRepositoryMongo.save(estudiosDocument);
        return estudiosMapperMongo.fromAdapterToDomain(savedDocument);
    }

    @Override
    public Boolean delete(Integer personId, Integer professionId) {
        log.debug("Into delete StudyEntity in Mongo DB with personId {} and professionId {}", personId, professionId);
        estudiosRepositoryMongo.deleteByCcPerAndIdProf(personId, professionId);
        return estudiosRepositoryMongo.findByCcPerAndIdProf(personId, professionId).isEmpty();
    }

    @Override
    public List<Study> find() {
        log.debug("Into find StudiesEntity in Mongo DB");
        return estudiosRepositoryMongo.findAll().stream()
                .map(estudiosMapperMongo::fromAdapterToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Study findById(Integer personId, Integer professionId) {
        log.debug("Into findById StudyEntity in Mongo DB with personId {} and professionId {}", personId, professionId);
        Optional<EstudiosDocument> optionalEstudiosDocument = estudiosRepositoryMongo.findByCcPerAndIdProf(personId, professionId);
        return optionalEstudiosDocument.map(estudiosMapperMongo::fromAdapterToDomain).orElse(null);
    }
} 