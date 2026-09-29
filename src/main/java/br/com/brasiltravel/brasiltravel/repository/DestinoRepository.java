package br.com.brasiltravel.brasiltravel.repository;

import br.com.brasiltravel.brasiltravel.model.Destino;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DestinoRepository extends JpaRepository<Destino, Long> {
    List<Destino> findByAtivoTrueOrderByCidadeAsc();
    List<Destino> findAllByOrderByCidadeAscEstadoAsc();
}
