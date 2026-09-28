package br.com.brasiltravel.brasiltravel_aereo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "solicitacao_voo")
public class SolicitacaoVoo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitacao_voo")
    private Long idSolicitacaoVoo;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_solicitacao", nullable = false)
    private SolicitacaoViagem solicitacao;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_voo", nullable = false)
    private Voo voo;

    @NotNull
    @Min(1)
    @Column(name = "qtd_passageiros", nullable = false)
    private Integer qtdPassageiros;

    @NotNull
    @Column(name = "ordem_voo", nullable = false)
    private Integer ordemVoo;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "preco_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "preco_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal precoTotal;

    public SolicitacaoVoo() {
    }

    public SolicitacaoVoo(SolicitacaoViagem solicitacao, Voo voo, Integer qtdPassageiros, Integer ordemVoo,
                          BigDecimal precoUnitario, BigDecimal precoTotal) {
        this.solicitacao = solicitacao;
        this.voo = voo;
        this.qtdPassageiros = qtdPassageiros;
        this.ordemVoo = ordemVoo;
        this.precoUnitario = precoUnitario;
        this.precoTotal = precoTotal;
    }

    public Long getIdSolicitacaoVoo() {
        return idSolicitacaoVoo;
    }

    public void setIdSolicitacaoVoo(Long idSolicitacaoVoo) {
        this.idSolicitacaoVoo = idSolicitacaoVoo;
    }

    public SolicitacaoViagem getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(SolicitacaoViagem solicitacao) {
        this.solicitacao = solicitacao;
    }

    public Voo getVoo() {
        return voo;
    }

    public void setVoo(Voo voo) {
        this.voo = voo;
    }

    public Integer getQtdPassageiros() {
        return qtdPassageiros;
    }

    public void setQtdPassageiros(Integer qtdPassageiros) {
        this.qtdPassageiros = qtdPassageiros;
    }

    public Integer getOrdemVoo() {
        return ordemVoo;
    }

    public void setOrdemVoo(Integer ordemVoo) {
        this.ordemVoo = ordemVoo;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal getPrecoTotal() {
        return precoTotal;
    }

    public void setPrecoTotal(BigDecimal precoTotal) {
        this.precoTotal = precoTotal;
    }
}
