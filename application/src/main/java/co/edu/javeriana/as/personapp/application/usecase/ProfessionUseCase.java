package co.edu.javeriana.as.personapp.application.usecase;

import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Profession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
public class ProfessionUseCase implements ProfessionInputPort {

    private ProfessionOutputPort professionPersistence;

    // Constructor for Spring to inject a default persistence adapter (e.g., MariaDB)
    public ProfessionUseCase(@Qualifier("professionOutputAdapterMaria") ProfessionOutputPort professionOutputPort) {
        this.professionPersistence = professionOutputPort;
    }

    @Override
    public void setPersistence(ProfessionOutputPort professionOutputPort) {
        this.professionPersistence = professionOutputPort;
    }

    @Override
    public Profession create(Profession profession) {
        log.debug("Into create Profession UseCase");
        return professionPersistence.save(profession);
    }

    @Override
    public Profession edit(Integer identification, Profession profession) throws NoExistException {
        log.debug("Into edit Profession UseCase");
        Profession oldProfession = professionPersistence.findById(identification);
        if (oldProfession != null) {
            // Ensure the ID from the path is set on the profession object to be saved, if it's not already.
            // This is important if the request body doesn't include the ID or if it's different.
            profession.setIdentification(identification);
            return professionPersistence.save(profession);
        }
        throw new NoExistException(
                "The profession with id " + identification + " does not exist into db, cannot be edited");
    }

    @Override
    public Boolean drop(Integer identification) throws NoExistException {
        log.debug("Into drop Profession UseCase");
        Profession oldProfession = professionPersistence.findById(identification);
        if (oldProfession != null) {
            return professionPersistence.delete(identification);
        }
        throw new NoExistException(
                "The profession with id " + identification + " does not exist into db, cannot be dropped");
    }

    @Override
    public List<Profession> findAll() {
        log.debug("Into findAll Profession UseCase");
        return professionPersistence.findAll();
    }

    @Override
    public Profession findOne(Integer identification) throws NoExistException {
        log.debug("Into findOne Profession UseCase");
        Profession profession = professionPersistence.findById(identification);
        if (profession != null) {
            return profession;
        }
        throw new NoExistException(
                "The profession with id " + identification + " does not exist into db, cannot be found");
    }

    @Override
    public Integer count() {
        log.debug("Into count Profession UseCase");
        return findAll().size();
    }
} 