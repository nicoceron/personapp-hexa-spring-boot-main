package co.edu.javeriana.as.personapp.mariadb.adapter;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntityPK;
import co.edu.javeriana.as.personapp.mariadb.mapper.EstudiosMapperMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.EstudiosRepositoryMaria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Adapter("studyOutputAdapterMaria")
@Transactional
public class StudyOutputAdapterMaria implements StudyOutputPort {

    private static final Logger log = LoggerFactory.getLogger(StudyOutputAdapterMaria.class);

    @Autowired
    private EstudiosRepositoryMaria estudiosRepositoryMaria;

    @Autowired
    private EstudiosMapperMaria estudiosMapperMaria;

    @Override
    public Study save(Study study) {
        log.debug("Into save StudyOutputAdapterMaria for study of person {} and profession {}", 
            study.getPerson().getIdentification(), study.getProfession().getIdentification());
        EstudiosEntity estudiosEntity = estudiosMapperMaria.fromDomainToAdapter(study);
        estudiosRepositoryMaria.save(estudiosEntity);
        return study; // Return the original domain object as it reflects the saved state
    }

    @Override
    public Boolean delete(Integer personId, Integer professionId) {
        estudiosRepositoryMaria.deleteById(new EstudiosEntityPK(professionId, personId));
        return estudiosRepositoryMaria.findById(new EstudiosEntityPK(professionId, personId)).isEmpty();
    }

    @Override
    public List<Study> find() {
        return estudiosRepositoryMaria.findAll().stream()
                .map(estudiosMapperMaria::fromAdapterToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Study findById(Integer personId, Integer professionId) {
        return estudiosRepositoryMaria.findById(new EstudiosEntityPK(professionId, personId))
                .map(estudiosMapperMaria::fromAdapterToDomain)
                .orElse(null);
    }
} 