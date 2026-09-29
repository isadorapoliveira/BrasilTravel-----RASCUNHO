package br.com.brasiltravel.brasiltravel.model;

import br.com.brasiltravel.brasiltravel.model.enums.ClasseVoo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "voos")
public class Voo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_voo")
    private Long idVoo;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_companhia", nullable = false)
    private CompanhiaAerea companhia;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aeroporto_origem", nullable = false)
    private Aeroporto aeroportoOrigem;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aeroporto_destino", nullable = false)
    private Aeroporto aeroportoDestino;

    @NotBlank
    @Column(name = "numero_voo", nullable = false, length = 12)
    private String numeroVoo;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "data_hora_partida", nullable = false)
    private LocalDateTime dataHoraPartida;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "data_hora_chegada", nullable = false)
    private LocalDateTime dataHoraChegada;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "preco_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoBase;

    @NotNull
    @Min(0)
    @Column(name = "capacidade_total")
    private Integer capacidadeTotal;

    @NotNull
    @Min(0)
    @Column(name = "vagas_disponiveis", nullable = false)
    private Integer vagasDisponiveis;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private ClasseVoo classe = ClasseVoo.ECONOMICA;

    @Column(nullable = false)
    private Boolean ativo = true;

    public Voo() {
    }

    public Voo(CompanhiaAerea companhia, Aeroporto aeroportoOrigem, Aeroporto aeroportoDestino, String numeroVoo,
               LocalDateTime dataHoraPartida, LocalDateTime dataHoraChegada, BigDecimal precoBase,
               Integer capacidadeTotal, Integer vagasDisponiveis, ClasseVoo classe, Boolean ativo) {
        this.companhia = companhia;
        this.aeroportoOrigem = aeroportoOrigem;
        this.aeroportoDestino = aeroportoDestino;
        this.numeroVoo = numeroVoo;
        this.dataHoraPartida = dataHoraPartida;
        this.dataHoraChegada = dataHoraChegada;
        this.precoBase = precoBase;
        this.capacidadeTotal = capacidadeTotal;
        this.vagasDisponiveis = vagasDisponiveis;
        this.classe = classe;
        this.ativo = ativo;
    }

    public Long getIdVoo() {
        return idVoo;
    }

    public void setIdVoo(Long idVoo) {
        this.idVoo = idVoo;
    }

    public CompanhiaAerea getCompanhia() {
        return companhia;
    }

    public void setCompanhia(CompanhiaAerea companhia) {
        this.companhia = companhia;
    }

    public Aeroporto getAeroportoOrigem() {
        return aeroportoOrigem;
    }

    public void setAeroportoOrigem(Aeroporto aeroportoOrigem) {
        this.aeroportoOrigem = aeroportoOrigem;
    }

    public Aeroporto getAeroportoDestino() {
        return aeroportoDestino;
    }

    public void setAeroportoDestino(Aeroporto aeroportoDestino) {
        this.aeroportoDestino = aeroportoDestino;
    }

    public String getNumeroVoo() {
        return numeroVoo;
    }

    public void setNumeroVoo(String numeroVoo) {
        this.numeroVoo = numeroVoo;
    }

    public LocalDateTime getDataHoraPartida() {
        return dataHoraPartida;
    }

    public void setDataHoraPartida(LocalDateTime dataHoraPartida) {
        this.dataHoraPartida = dataHoraPartida;
    }

    public LocalDateTime getDataHoraChegada() {
        return dataHoraChegada;
    }

    public void setDataHoraChegada(LocalDateTime dataHoraChegada) {
        this.dataHoraChegada = dataHoraChegada;
    }

    public BigDecimal getPrecoBase() {
        return precoBase;
    }

    public void setPrecoBase(BigDecimal precoBase) {
        this.precoBase = precoBase;
    }

    public Integer getCapacidadeTotal() {
        return capacidadeTotal;
    }

    public void setCapacidadeTotal(Integer capacidadeTotal) {
        this.capacidadeTotal = capacidadeTotal;
    }

    public Integer getVagasDisponiveis() {
        return vagasDisponiveis;
    }

    public void setVagasDisponiveis(Integer vagasDisponiveis) {
        this.vagasDisponiveis = vagasDisponiveis;
    }

    public ClasseVoo getClasse() {
        return classe;
    }

    public void setClasse(ClasseVoo classe) {
        this.classe = classe;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
