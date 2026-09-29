package br.com.brasiltravel.brasiltravel.repository;

import br.com.brasiltravel.brasiltravel.model.SolicitacaoVoo;
import br.com.brasiltravel.brasiltravel.model.enums.StatusSolicitacao;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SolicitacaoVooRepository extends JpaRepository<SolicitacaoVoo, Long> {
    long countByVoo_IdVoo(Long idVoo);

    List<SolicitacaoVoo> findBySolicitacao_IdSolicitacaoOrderByOrdemVooAsc(Long idSolicitacao);

    @EntityGraph(attributePaths = {
            "solicitacao",
            "solicitacao.usuario",
            "solicitacao.destinoPrincipal",
            "voo",
            "voo.companhia",
            "voo.aeroportoOrigem",
            "voo.aeroportoOrigem.destino",
            "voo.aeroportoDestino",
            "voo.aeroportoDestino.destino"
    })
    @Query("""
            select sv
            from SolicitacaoVoo sv
            join sv.solicitacao s
            where s.dataCriacao between :inicio and :fim
              and s.statusSolicitacao = :status
            order by s.dataCriacao asc, sv.ordemVoo asc
            """)
    List<SolicitacaoVoo> buscarParaRelatorioPeriodoStatus(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            @Param("status") StatusSolicitacao status
    );
}
