package co.edu.javeriana.as.personapp.mongo.mapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument;
import co.edu.javeriana.as.personapp.mongo.repository.PersonaRepositoryMongo;
import co.edu.javeriana.as.personapp.mongo.repository.ProfesionRepositoryMongo;
import lombok.NonNull;

@Mapper
public class EstudiosMapperMongo {

	@Autowired
	protected PersonaMapperMongo personaMapperMongo;
	@Autowired
	protected ProfesionMapperMongo profesionMapperMongo;
	@Autowired
	protected PersonaRepositoryMongo personaRepositoryMongo;
	@Autowired
	protected ProfesionRepositoryMongo profesionRepositoryMongo;

	public EstudiosDocument fromDomainToAdapter(Study study) {
		if (study == null) {
			return null;
		}
		
		// For @DocumentReference, we primarily need the ID of the referenced document.
		// The actual PersonaDocument and ProfesionDocument will be fetched by Spring Data MongoDB
		// when the EstudiosDocument is loaded, based on these IDs.
		PersonaDocument refPersonaDoc = new PersonaDocument();
		if (study.getPerson() != null && study.getPerson().getIdentification() != null) {
		    refPersonaDoc.setId(study.getPerson().getIdentification());
		} else {
		    // This indicates a problem with the incoming Study domain object from the UseCase/REST adapter
		    throw new IllegalArgumentException("Cannot map Study to EstudiosDocument: Study's Person or Person ID is null.");
		}

		ProfesionDocument refProfesionDoc = new ProfesionDocument();
		if (study.getProfession() != null && study.getProfession().getIdentification() != null) {
		    refProfesionDoc.setId(study.getProfession().getIdentification());
		} else {
		    throw new IllegalArgumentException("Cannot map Study to EstudiosDocument: Study's Profession or Profession ID is null.");
		}

		// The EstudiosDocument constructor will set its own idProf and ccPer fields,
		// and also use refPersonaDoc and refProfesionDoc for the @DocumentReference fields.
		return new EstudiosDocument(
				study.getProfession().getIdentification(),
				study.getPerson().getIdentification(),
				study.getGraduationDate(),
				study.getUniversityName(),
				refPersonaDoc,   // Pass the reference-only document
				refProfesionDoc  // Pass the reference-only document
		);
	}

	public Study fromAdapterToDomain(EstudiosDocument estudiosDocument) {
		if (estudiosDocument == null) {
			return null;
		}

		Person person = null;
		// Fetch PersonaDocument using the ccPer from EstudiosDocument
		if (estudiosDocument.getCcPer() != null) {
			PersonaDocument personaDocFromRepo = personaRepositoryMongo.findById(estudiosDocument.getCcPer()).orElse(null);
			if (personaDocFromRepo != null) {
				person = personaMapperMongo.fromAdapterToDomain(personaDocFromRepo);
			} else {
				// Log that person was not found, create a placeholder if necessary or handle error
				// For now, let's allow it to be null if not found, or create a minimal one
                person = new Person();
                person.setIdentification(estudiosDocument.getCcPer());
                // Other fields will be null, which might be acceptable depending on use case
			}
		} else {
            // Handle case where ccPer itself is null in EstudiosDocument
        }

		Profession profession = null;
		// Fetch ProfesionDocument using the idProf from EstudiosDocument
		if (estudiosDocument.getIdProf() != null) {
			ProfesionDocument profesionDocFromRepo = profesionRepositoryMongo.findById(estudiosDocument.getIdProf()).orElse(null);
			if (profesionDocFromRepo != null) {
				profession = profesionMapperMongo.fromAdapterToDomain(profesionDocFromRepo);
			} else {
                profession = new Profession();
                profession.setIdentification(estudiosDocument.getIdProf());
                 // Other fields will be null
			}
		} else {
            // Handle case where idProf itself is null in EstudiosDocument
        }

		// If person or profession is still null here, it means they weren't found or IDs were missing.
        // The Study constructor requires non-null Person and Profession.
        if (person == null) {
            // This should ideally not happen if data integrity is maintained
            // or if placeholder logic above is more robust.
            throw new IllegalStateException("Person could not be resolved for Study with ccPer: " + estudiosDocument.getCcPer());
        }
        if (profession == null) {
            throw new IllegalStateException("Profession could not be resolved for Study with idProf: " + estudiosDocument.getIdProf());
        }

		LocalDate graduationDate = estudiosDocument.getFecha(); // Assuming fecha is already LocalDate
		return new Study(person, profession, graduationDate, estudiosDocument.getUniver());
	}

	public List<Study> fromAdapterListToDomainList(List<EstudiosDocument> estudiosDocuments) {
	    if (estudiosDocuments == null) {
	        return new ArrayList<>();
	    }
	    return estudiosDocuments.stream()
	                            .map(this::fromAdapterToDomain)
	                            .collect(Collectors.toList());
	}
}