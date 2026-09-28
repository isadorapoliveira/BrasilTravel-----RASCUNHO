package br.com.brasiltravel.brasiltravel_aereo.repository;

import br.com.brasiltravel.brasiltravel_aereo.model.Destino;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DestinoRepository extends JpaRepository<Destino, Long> {
    List<Destino> findByAtivoTrueOrderByCidadeAsc();
    List<Destino> findAllByOrderByCidadeAscEstadoAsc();
}
