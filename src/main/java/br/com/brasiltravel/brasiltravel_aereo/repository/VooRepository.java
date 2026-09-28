package br.com.brasiltravel.brasiltravel_aereo.repository;

import br.com.brasiltravel.brasiltravel_aereo.model.Aeroporto;
import br.com.brasiltravel.brasiltravel_aereo.model.Voo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VooRepository extends JpaRepository<Voo, Long> {
    @EntityGraph(attributePaths = {"companhia", "aeroportoOrigem", "aeroportoDestino", "aeroportoOrigem.destino", "aeroportoDestino.destino"})
    List<Voo> findAllByOrderByDataHoraPartidaAsc();

    @Query(value = """
            select v from Voo v
            join fetch v.companhia c
            join fetch v.aeroportoOrigem ao
            join fetch ao.destino doo
            join fetch v.aeroportoDestino ad
            join fetch ad.destino dd
            where v.dataHoraPartida >= :inicio
              and (:idOrigem is null or ao.idAeroporto = :idOrigem)
              and (:idDestino is null or ad.idAeroporto = :idDestino)
            order by v.dataHoraPartida asc
            """,
            countQuery = """
            select count(v) from Voo v
            join v.aeroportoOrigem ao
            join v.aeroportoDestino ad
            where v.dataHoraPartida >= :inicio
              and (:idOrigem is null or ao.idAeroporto = :idOrigem)
              and (:idDestino is null or ad.idAeroporto = :idDestino)
            """)
    Page<Voo> buscarVoosFuturosFiltrados(@Param("inicio") LocalDateTime inicio,
                                          @Param("idOrigem") Long idOrigem,
                                          @Param("idDestino") Long idDestino,
                                          Pageable pageable);

    @EntityGraph(attributePaths = {"companhia", "aeroportoOrigem", "aeroportoDestino", "aeroportoOrigem.destino", "aeroportoDestino.destino"})
    List<Voo> findByAtivoTrueOrderByDataHoraPartidaAsc();

    @EntityGraph(attributePaths = {"companhia", "aeroportoOrigem", "aeroportoDestino", "aeroportoOrigem.destino", "aeroportoDestino.destino"})
    Optional<Voo> findByIdVoo(Long idVoo);

    @EntityGraph(attributePaths = {"companhia", "aeroportoOrigem", "aeroportoDestino", "aeroportoOrigem.destino", "aeroportoDestino.destino"})
    List<Voo> findByAtivoTrueAndVagasDisponiveisGreaterThanOrderByDataHoraPartidaAsc(Integer vagasDisponiveis);

    @EntityGraph(attributePaths = {"companhia", "aeroportoOrigem", "aeroportoDestino"})
    List<Voo> findByAeroportoOrigemAndAeroportoDestinoAndDataHoraPartidaBetweenAndAtivoTrueOrderByDataHoraPartidaAsc(
            Aeroporto origem,
            Aeroporto destino,
            LocalDateTime inicio,
            LocalDateTime fim
    );

    boolean existsByNumeroVooIgnoreCase(String numeroVoo);
    long countByAeroportoOrigem_IdAeroporto(Long idAeroporto);
    long countByAeroportoDestino_IdAeroporto(Long idAeroporto);
    long countByCompanhia_IdCompanhia(Long idCompanhia);
}
