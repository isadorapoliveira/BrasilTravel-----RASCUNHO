package br.com.brasiltravel.brasiltravel_aereo.dto;

import java.math.BigDecimal;

public class RelatorioDestinoDTO {
    private final String periodo;
    private final String destino;
    private final Long quantidadeSolicitacoes;
    private final Long quantidadePassageiros;
    private final BigDecimal receitaTotal;
    private final BigDecimal ticketMedio;

    public RelatorioDestinoDTO(String periodo, String destino, Long quantidadeSolicitacoes,
                               Long quantidadePassageiros, BigDecimal receitaTotal, BigDecimal ticketMedio) {
        this.periodo = periodo;
        this.destino = destino;
        this.quantidadeSolicitacoes = quantidadeSolicitacoes == null ? 0L : quantidadeSolicitacoes;
        this.quantidadePassageiros = quantidadePassageiros == null ? 0L : quantidadePassageiros;
        this.receitaTotal = receitaTotal == null ? BigDecimal.ZERO : receitaTotal;
        this.ticketMedio = ticketMedio == null ? BigDecimal.ZERO : ticketMedio;
    }

    public String getPeriodo() { return periodo; }
    public String getDestino() { return destino; }
    public Long getQuantidadeSolicitacoes() { return quantidadeSolicitacoes; }
    public Long getQuantidadePassageiros() { return quantidadePassageiros; }
    public BigDecimal getReceitaTotal() { return receitaTotal; }
    public BigDecimal getTicketMedio() { return ticketMedio; }
}
