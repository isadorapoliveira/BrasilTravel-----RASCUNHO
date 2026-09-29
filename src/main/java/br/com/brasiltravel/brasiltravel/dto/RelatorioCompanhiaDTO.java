package br.com.brasiltravel.brasiltravel.dto;

import java.math.BigDecimal;

public class RelatorioCompanhiaDTO {
    private final String periodo;
    private final String companhia;
    private final Long quantidadeSolicitacoes;
    private final Long quantidadePassageiros;
    private final BigDecimal receitaTotal;
    private final BigDecimal precoMedio;

    public RelatorioCompanhiaDTO(String companhia, Long quantidadeSolicitacoes, Long quantidadePassageiros, BigDecimal receitaTotal) {
        this("Geral", companhia, quantidadeSolicitacoes, quantidadePassageiros, receitaTotal, BigDecimal.ZERO);
    }

    public RelatorioCompanhiaDTO(String periodo, String companhia, Long quantidadeSolicitacoes, Long quantidadePassageiros, BigDecimal receitaTotal, BigDecimal precoMedio) {
        this.periodo = periodo;
        this.companhia = companhia;
        this.quantidadeSolicitacoes = quantidadeSolicitacoes == null ? 0L : quantidadeSolicitacoes;
        this.quantidadePassageiros = quantidadePassageiros == null ? 0L : quantidadePassageiros;
        this.receitaTotal = receitaTotal == null ? BigDecimal.ZERO : receitaTotal;
        this.precoMedio = precoMedio == null ? BigDecimal.ZERO : precoMedio;
    }

    public String getPeriodo() { return periodo; }
    public String getCompanhia() { return companhia; }
    public Long getQuantidadeSolicitacoes() { return quantidadeSolicitacoes; }
    public Long getQuantidadePassageiros() { return quantidadePassageiros; }
    public BigDecimal getReceitaTotal() { return receitaTotal; }
    public BigDecimal getPrecoMedio() { return precoMedio; }
}
