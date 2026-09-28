package br.com.brasiltravel.brasiltravel_aereo.dto;

import java.math.BigDecimal;

public class RelatorioTrajetoDTO {
    private final String periodo;
    private final String origem;
    private final String destino;
    private final Long quantidadeSolicitacoes;
    private final Long quantidadePassageiros;
    private final BigDecimal precoMedio;
    private final BigDecimal menorPreco;
    private final BigDecimal maiorPreco;
    private final BigDecimal receitaTotal;

    public RelatorioTrajetoDTO(String origem, String destino, Long quantidadeSolicitacoes, Double precoMedio, BigDecimal menorPreco, BigDecimal maiorPreco) {
        this("Geral", origem, destino, quantidadeSolicitacoes, 0L,
                precoMedio == null ? BigDecimal.ZERO : BigDecimal.valueOf(precoMedio),
                menorPreco, maiorPreco, BigDecimal.ZERO);
    }

    public RelatorioTrajetoDTO(String periodo, String origem, String destino, Long quantidadeSolicitacoes, Long quantidadePassageiros,
                               BigDecimal precoMedio, BigDecimal menorPreco, BigDecimal maiorPreco, BigDecimal receitaTotal) {
        this.periodo = periodo;
        this.origem = origem;
        this.destino = destino;
        this.quantidadeSolicitacoes = quantidadeSolicitacoes == null ? 0L : quantidadeSolicitacoes;
        this.quantidadePassageiros = quantidadePassageiros == null ? 0L : quantidadePassageiros;
        this.precoMedio = precoMedio == null ? BigDecimal.ZERO : precoMedio;
        this.menorPreco = menorPreco == null ? BigDecimal.ZERO : menorPreco;
        this.maiorPreco = maiorPreco == null ? BigDecimal.ZERO : maiorPreco;
        this.receitaTotal = receitaTotal == null ? BigDecimal.ZERO : receitaTotal;
    }

    public String getPeriodo() { return periodo; }
    public String getOrigem() { return origem; }
    public String getDestino() { return destino; }
    public Long getQuantidadeSolicitacoes() { return quantidadeSolicitacoes; }
    public Long getQuantidadePassageiros() { return quantidadePassageiros; }
    public BigDecimal getPrecoMedio() { return precoMedio; }
    public BigDecimal getMenorPreco() { return menorPreco; }
    public BigDecimal getMaiorPreco() { return maiorPreco; }
    public BigDecimal getReceitaTotal() { return receitaTotal; }
}
