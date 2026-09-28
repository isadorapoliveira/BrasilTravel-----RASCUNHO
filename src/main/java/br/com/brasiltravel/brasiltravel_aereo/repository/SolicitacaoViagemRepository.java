package br.com.brasiltravel.brasiltravel_aereo.repository;

import br.com.brasiltravel.brasiltravel_aereo.model.SolicitacaoViagem;
import br.com.brasiltravel.brasiltravel_aereo.model.Usuario;
import br.com.brasiltravel.brasiltravel_aereo.model.enums.StatusSolicitacao;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SolicitacaoViagemRepository extends JpaRepository<SolicitacaoViagem, Long> {
    @EntityGraph(attributePaths = {
            "usuario",
            "destinoPrincipal",
            "voosSolicitados",
            "voosSolicitados.voo",
            "voosSolicitados.voo.companhia",
            "voosSolicitados.voo.aeroportoOrigem",
            "voosSolicitados.voo.aeroportoOrigem.destino",
            "voosSolicitados.voo.aeroportoDestino",
            "voosSolicitados.voo.aeroportoDestino.destino"
    })
    List<SolicitacaoViagem> findByUsuarioOrderByDataCriacaoDesc(Usuario usuario);

    @EntityGraph(attributePaths = {
            "usuario",
            "destinoPrincipal",
            "voosSolicitados",
            "voosSolicitados.voo",
            "voosSolicitados.voo.companhia",
            "voosSolicitados.voo.aeroportoOrigem",
            "voosSolicitados.voo.aeroportoOrigem.destino",
            "voosSolicitados.voo.aeroportoDestino",
            "voosSolicitados.voo.aeroportoDestino.destino"
    })
    List<SolicitacaoViagem> findAllByOrderByDataCriacaoDesc();

    @EntityGraph(attributePaths = {
            "usuario",
            "destinoPrincipal",
            "voosSolicitados",
            "voosSolicitados.voo",
            "voosSolicitados.voo.companhia",
            "voosSolicitados.voo.aeroportoOrigem",
            "voosSolicitados.voo.aeroportoOrigem.destino",
            "voosSolicitados.voo.aeroportoDestino",
            "voosSolicitados.voo.aeroportoDestino.destino"
    })
    Optional<SolicitacaoViagem> findByIdSolicitacao(Long idSolicitacao);

    @EntityGraph(attributePaths = {
            "usuario",
            "destinoPrincipal",
            "voosSolicitados",
            "voosSolicitados.voo",
            "voosSolicitados.voo.companhia",
            "voosSolicitados.voo.aeroportoOrigem",
            "voosSolicitados.voo.aeroportoOrigem.destino",
            "voosSolicitados.voo.aeroportoDestino",
            "voosSolicitados.voo.aeroportoDestino.destino"
    })
    List<SolicitacaoViagem> findByStatusSolicitacaoOrderByDataCriacaoDesc(StatusSolicitacao statusSolicitacao);

    @EntityGraph(attributePaths = {"usuario", "destinoPrincipal"})
    List<SolicitacaoViagem> findByDataCriacaoBetweenOrderByDataCriacaoDesc(LocalDateTime inicio, LocalDateTime fim);


    @EntityGraph(attributePaths = {
            "usuario",
            "destinoPrincipal",
            "voosSolicitados",
            "voosSolicitados.voo",
            "voosSolicitados.voo.companhia",
            "voosSolicitados.voo.aeroportoOrigem",
            "voosSolicitados.voo.aeroportoOrigem.destino",
            "voosSolicitados.voo.aeroportoDestino",
            "voosSolicitados.voo.aeroportoDestino.destino"
    })
    @Query("""
            select distinct s
            from SolicitacaoViagem s
            where (:status is null or s.statusSolicitacao = :status)
              and (:idCliente is null or s.usuario.idUsuario = :idCliente)
            order by s.dataCriacao desc
            """)
    List<SolicitacaoViagem> filtrarAdmin(
            @Param("status") StatusSolicitacao status,
            @Param("idCliente") Long idCliente
    );

    @EntityGraph(attributePaths = {"usuario", "destinoPrincipal", "voosSolicitados", "voosSolicitados.voo"})
    @Query("""
            select distinct s
            from SolicitacaoViagem s
            left join s.voosSolicitados sv
            left join sv.voo v
            where s.dataCriacao between :inicio and :fim
              and (:idCompanhia is null or v.companhia.idCompanhia = :idCompanhia)
              and (:idOrigem is null or v.aeroportoOrigem.idAeroporto = :idOrigem)
              and (:idDestinoAeroporto is null or v.aeroportoDestino.idAeroporto = :idDestinoAeroporto)
              and (:idDestinoPrincipal is null or s.destinoPrincipal.idDestino = :idDestinoPrincipal)
            order by s.dataCriacao desc
            """)
    List<SolicitacaoViagem> relatorioSolicitacoesFiltrado(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            @Param("idCompanhia") Long idCompanhia,
            @Param("idOrigem") Long idOrigem,
            @Param("idDestinoAeroporto") Long idDestinoAeroporto,
            @Param("idDestinoPrincipal") Long idDestinoPrincipal
    );

    long countByDestinoPrincipal_IdDestino(Long idDestino);
}
