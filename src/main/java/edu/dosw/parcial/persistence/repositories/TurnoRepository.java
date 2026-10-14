package edu.dosw.parcial.persistence.repositories;

import edu.dosw.parcial.persistence.entities.TurnoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TurnoRepository extends MongoRepository<TurnoDocument, String> {
    Optional<TurnoDocument> findByBarberoIdAndFechaAndHora(String barberoId, String fecha, String hora);
    Optional<TurnoDocument> findByTurnoId(String turnoId);
    List<TurnoDocument> findByFechaAndHoraAndEstado(String fecha, String hora, String estado);
    List<TurnoDocument> findByBarberoIdAndFechaAndHoraAndEstado(String barberoId, String fecha, String hora, String estado);
    List<TurnoDocument> findByFechaAndHora(String fecha, String hora);
}
