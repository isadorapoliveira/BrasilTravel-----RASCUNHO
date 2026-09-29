package br.com.brasiltravel.brasiltravel.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RelatorioOcupacaoDTO {
    private final String periodo;
    private final String numeroVoo;
    private final String companhia;
    private final String origem;
    private final String destino;
    private final LocalDateTime dataHoraPartida;
    private final String classe;
    private final Integer capacidadeTotal;
    private final Long passageirosReservados;
    private final Integer vagasDisponiveis;
    private final BigDecimal taxaOcupacao;
    private final BigDecimal receitaTotal;

    public RelatorioOcupacaoDTO(String periodo,
                                String numeroVoo,
                                String companhia,
                                String origem,
                                String destino,
                                LocalDateTime dataHoraPartida,
                                String classe,
                                Integer capacidadeTotal,
                                Long passageirosReservados,
                                Integer vagasDisponiveis,
                                BigDecimal taxaOcupacao,
                                BigDecimal receitaTotal) {
        this.periodo = periodo;
        this.numeroVoo = numeroVoo;
        this.companhia = companhia;
        this.origem = origem;
        this.destino = destino;
        this.dataHoraPartida = dataHoraPartida;
        this.classe = classe;
        this.capacidadeTotal = capacidadeTotal == null ? 0 : capacidadeTotal;
        this.passageirosReservados = passageirosReservados == null ? 0L : passageirosReservados;
        this.vagasDisponiveis = vagasDisponiveis == null ? 0 : vagasDisponiveis;
        this.taxaOcupacao = taxaOcupacao == null ? BigDecimal.ZERO : taxaOcupacao;
        this.receitaTotal = receitaTotal == null ? BigDecimal.ZERO : receitaTotal;
    }

    public String getPeriodo() { return periodo; }
    public String getNumeroVoo() { return numeroVoo; }
    public String getCompanhia() { return companhia; }
    public String getOrigem() { return origem; }
    public String getDestino() { return destino; }
    public LocalDateTime getDataHoraPartida() { return dataHoraPartida; }
    public String getClasse() { return classe; }
    public Integer getCapacidadeTotal() { return capacidadeTotal; }
    public Long getPassageirosReservados() { return passageirosReservados; }
    public Integer getVagasDisponiveis() { return vagasDisponiveis; }
    public BigDecimal getTaxaOcupacao() { return taxaOcupacao; }
    public BigDecimal getReceitaTotal() { return receitaTotal; }
}
