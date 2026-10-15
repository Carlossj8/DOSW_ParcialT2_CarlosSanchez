package edu.dosw.parcial.persistence.repositories;

import edu.dosw.parcial.persistence.entities.BarberoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BarberoRepository extends MongoRepository<BarberoDocument, String> {
    Optional<BarberoDocument> findByNombreIgnoreCase(String nombre);
}
