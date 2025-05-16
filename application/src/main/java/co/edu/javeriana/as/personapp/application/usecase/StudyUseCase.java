package co.edu.javeriana.as.personapp.application.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Study;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
public class StudyUseCase implements StudyInputPort {

    private StudyOutputPort studyPersintence;

    public StudyUseCase(@Qualifier("studyOutputAdapterMaria") StudyOutputPort studyPersintence) {
        this.studyPersintence = studyPersintence;
    }

    @Override
    public void setPersintence(StudyOutputPort studyPersintence) {
        this.studyPersintence = studyPersintence;
    }

    @Override
    public Study create(Study study) throws NoExistException {
        if (studyPersintence.findById(study.getPerson().getIdentification(), study.getProfession().getIdentification()) == null) {
            return studyPersintence.save(study);
        }
        throw new NoExistException(
                "Cannot create study, already exists with personId: " + study.getPerson().getIdentification()
                        + " and professionId: " + study.getProfession().getIdentification());
    }

    @Override
    public Study edit(Integer personId, Integer professionId, Study study) throws NoExistException {
        if (studyPersintence.findById(personId, professionId) != null) {
            study.getPerson().setIdentification(personId);
            study.getProfession().setIdentification(professionId);
            return studyPersintence.save(study);
        }
        throw new NoExistException(
                "Cannot edit study, does not exist with personId: " + personId + " and professionId: "
                        + professionId);
    }

    @Override
    public Boolean drop(Integer personId, Integer professionId) throws NoExistException {
        if (studyPersintence.findById(personId, professionId) != null) {
            return studyPersintence.delete(personId, professionId);
        }
        throw new NoExistException(
                "Cannot drop study, does not exist with personId: " + personId + " and professionId: "
                        + professionId);
    }

    @Override
    public List<Study> findAll() {
        log.info("Listing all studies from persistance");
        return studyPersintence.find();
    }

    @Override
    public Study findOne(Integer personId, Integer professionId) throws NoExistException {
        Study study = studyPersintence.findById(personId, professionId);
        if (study != null) {
            return study;
        }
        throw new NoExistException(
                "Study with personId " + personId + " and professionId " + professionId + " does not exist");
    }

    @Override
    public Integer count() {
        return findAll().size();
    }
} 