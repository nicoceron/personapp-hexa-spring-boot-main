package co.edu.javeriana.as.personapp.mongo.repository;

import co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelefonoRepositoryMongo extends MongoRepository<TelefonoDocument, String> {
    List<TelefonoDocument> findByDuenio(Integer duenioId);
} 