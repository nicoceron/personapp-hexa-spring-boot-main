package co.edu.javeriana.as.personapp.application.port.in;

import java.util.List;

import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Study;

@Port
public interface StudyInputPort {
    void setPersintence(StudyOutputPort studyOutputPort);

    Study create(Study study) throws NoExistException;

    Study edit(Integer personId, Integer professionId, Study study) throws NoExistException;

    Boolean drop(Integer personId, Integer professionId) throws NoExistException;

    List<Study> findAll();

    Study findOne(Integer personId, Integer professionId) throws NoExistException;

    Integer count();
} 