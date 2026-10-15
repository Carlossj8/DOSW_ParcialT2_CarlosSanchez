package edu.dosw.parcial.persistence.repositories;

import edu.dosw.parcial.persistence.entities.ReservaDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends MongoRepository<ReservaDocument, String> {
    long countByFechaAndHoraAndEstado(String fecha, String hora, String estado);
    List<ReservaDocument> findByClienteCorreoOrderByFechaDescHoraDesc(String correo);
    List<ReservaDocument> findAllByOrderByFechaDescHoraDesc();
    boolean existsByBarberoAsignadoAndFechaAndHoraAndEstado(String barberoAsignado, String fecha, String hora, String estado);
}
