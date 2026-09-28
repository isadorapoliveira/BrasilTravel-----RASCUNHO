package br.com.brasiltravel.brasiltravel_aereo.model;

import br.com.brasiltravel.brasiltravel_aereo.model.enums.StatusSolicitacao;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "solicitacao_viagem")
public class SolicitacaoViagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitacao")
    private Long idSolicitacao;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_destino_principal", nullable = false)
    private Destino destinoPrincipal;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @NotNull
    @Column(name = "data_ida", nullable = false)
    private LocalDate dataIda;

    @Column(name = "data_volta")
    private LocalDate dataVolta;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_solicitacao", nullable = false, length = 30)
    private StatusSolicitacao statusSolicitacao = StatusSolicitacao.AGUARDANDO_ATENDIMENTO;

    @Column(name = "valor_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @OneToMany(mappedBy = "solicitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SolicitacaoVoo> voosSolicitados = new ArrayList<>();

    public SolicitacaoViagem() {
    }

    public SolicitacaoViagem(Usuario usuario, Destino destinoPrincipal, LocalDate dataIda, LocalDate dataVolta,
                             StatusSolicitacao statusSolicitacao, BigDecimal valorTotal, String observacoes) {
        this.usuario = usuario;
        this.destinoPrincipal = destinoPrincipal;
        this.dataIda = dataIda;
        this.dataVolta = dataVolta;
        this.statusSolicitacao = statusSolicitacao;
        this.valorTotal = valorTotal;
        this.observacoes = observacoes;
    }

    @PrePersist
    public void prePersist() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
        if (statusSolicitacao == null) {
            statusSolicitacao = StatusSolicitacao.AGUARDANDO_ATENDIMENTO;
        }
        if (valorTotal == null) {
            valorTotal = BigDecimal.ZERO;
        }
    }

    public void adicionarVoo(SolicitacaoVoo solicitacaoVoo) {
        voosSolicitados.add(solicitacaoVoo);
        solicitacaoVoo.setSolicitacao(this);
    }

    public Long getIdSolicitacao() {
        return idSolicitacao;
    }

    public void setIdSolicitacao(Long idSolicitacao) {
        this.idSolicitacao = idSolicitacao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Destino getDestinoPrincipal() {
        return destinoPrincipal;
    }

    public void setDestinoPrincipal(Destino destinoPrincipal) {
        this.destinoPrincipal = destinoPrincipal;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDate getDataIda() {
        return dataIda;
    }

    public void setDataIda(LocalDate dataIda) {
        this.dataIda = dataIda;
    }

    public LocalDate getDataVolta() {
        return dataVolta;
    }

    public void setDataVolta(LocalDate dataVolta) {
        this.dataVolta = dataVolta;
    }

    public StatusSolicitacao getStatusSolicitacao() {
        return statusSolicitacao;
    }

    public void setStatusSolicitacao(StatusSolicitacao statusSolicitacao) {
        this.statusSolicitacao = statusSolicitacao;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public List<SolicitacaoVoo> getVoosSolicitados() {
        return voosSolicitados;
    }

    public void setVoosSolicitados(List<SolicitacaoVoo> voosSolicitados) {
        this.voosSolicitados = voosSolicitados;
    }
}
