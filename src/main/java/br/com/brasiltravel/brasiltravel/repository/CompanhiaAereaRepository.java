package br.com.brasiltravel.brasiltravel.repository;

import br.com.brasiltravel.brasiltravel.model.CompanhiaAerea;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanhiaAereaRepository extends JpaRepository<CompanhiaAerea, Long> {
    List<CompanhiaAerea> findAllByOrderByNomeAsc();
    List<CompanhiaAerea> findByAtivoTrueOrderByNomeAsc();
    Optional<CompanhiaAerea> findByIdCompanhia(Long idCompanhia);
    boolean existsByCodigoIataIgnoreCase(String codigoIata);
}
