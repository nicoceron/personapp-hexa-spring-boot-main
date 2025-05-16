package co.edu.javeriana.as.personapp.mongo.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;

@Repository
public interface EstudiosRepositoryMongo extends MongoRepository<EstudiosDocument, String> {
    // Define a method to find by the logical composite key parts
    Optional<EstudiosDocument> findByCcPerAndIdProf(Integer ccPer, Integer idProf);

    // Define a method to delete by the logical composite key parts
    void deleteByCcPerAndIdProf(Integer ccPer, Integer idProf);
} 