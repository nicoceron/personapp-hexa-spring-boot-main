package co.edu.javeriana.as.personapp.terminal.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.terminal.model.ProfesionModelCli;

@Mapper
public class ProfesionMapperCli {

    public ProfesionModelCli fromDomainToAdapterCli(Profession profession) {
        if (profession == null) {
            return null;
        }
        ProfesionModelCli profesionModelCli = new ProfesionModelCli();
        profesionModelCli.setIdentification(profession.getIdentification());
        profesionModelCli.setName(profession.getName());
        profesionModelCli.setDescription(profession.getDescription());
        return profesionModelCli;
    }

    public Profession fromAdapterCliToDomain(ProfesionModelCli profesionModelCli) {
        if (profesionModelCli == null) {
            return null;
        }
        Profession profession = new Profession();
        profession.setIdentification(profesionModelCli.getIdentification());
        profession.setName(profesionModelCli.getName());
        profession.setDescription(profesionModelCli.getDescription());
        // Note: studies and person fields in Profession domain object are not set here,
        // as they are typically handled by the use case or persistence layer
        // when establishing relationships. This mapper focuses on direct field mapping.
        return profession;
    }
}
