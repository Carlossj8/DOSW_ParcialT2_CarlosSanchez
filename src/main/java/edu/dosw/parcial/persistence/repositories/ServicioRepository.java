package edu.dosw.parcial.persistence.repositories;

import edu.dosw.parcial.persistence.entities.ServicioDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicioRepository extends MongoRepository<ServicioDocument, String> {
    Optional<ServicioDocument> findByCodigo(String codigo);
    List<ServicioDocument> findByActivoTrue();
}
