package br.com.brasiltravel.brasiltravel.repository;

import br.com.brasiltravel.brasiltravel.model.Aeroporto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AeroportoRepository extends JpaRepository<Aeroporto, Long> {
    @EntityGraph(attributePaths = {"destino"})
    List<Aeroporto> findAllByOrderByCodigoIataAsc();

    @EntityGraph(attributePaths = {"destino"})
    List<Aeroporto> findByAtivoTrueOrderByCodigoIataAsc();

    @EntityGraph(attributePaths = {"destino"})
    Optional<Aeroporto> findByIdAeroporto(Long idAeroporto);

    boolean existsByCodigoIataIgnoreCase(String codigoIata);
    long countByDestino_IdDestino(Long idDestino);
}
