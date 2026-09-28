package br.com.brasiltravel.brasiltravel_aereo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public class NovaSolicitacaoForm {

    @NotNull(message = "Selecione o destino da viagem.")
    private Long idDestinoPrincipal;

    @NotNull(message = "Selecione o aeroporto de partida.")
    private Long idAeroportoPartida;

    @NotNull(message = "Selecione o voo de ida.")
    private Long idVooIda;

    private Long idVooVolta;

    @NotNull(message = "Informe a data de ida.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataIda;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataVolta;

    @NotNull(message = "Informe a quantidade de passageiros.")
    @Min(value = 1, message = "A quantidade mínima é 1 passageiro.")
    private Integer qtdPassageiros = 1;

    private String observacoes;

    public Long getIdDestinoPrincipal() { return idDestinoPrincipal; }
    public void setIdDestinoPrincipal(Long idDestinoPrincipal) { this.idDestinoPrincipal = idDestinoPrincipal; }
    public Long getIdAeroportoPartida() { return idAeroportoPartida; }
    public void setIdAeroportoPartida(Long idAeroportoPartida) { this.idAeroportoPartida = idAeroportoPartida; }
    public Long getIdVooIda() { return idVooIda; }
    public void setIdVooIda(Long idVooIda) { this.idVooIda = idVooIda; }
    public Long getIdVooVolta() { return idVooVolta; }
    public void setIdVooVolta(Long idVooVolta) { this.idVooVolta = idVooVolta; }
    public LocalDate getDataIda() { return dataIda; }
    public void setDataIda(LocalDate dataIda) { this.dataIda = dataIda; }
    public LocalDate getDataVolta() { return dataVolta; }
    public void setDataVolta(LocalDate dataVolta) { this.dataVolta = dataVolta; }
    public Integer getQtdPassageiros() { return qtdPassageiros; }
    public void setQtdPassageiros(Integer qtdPassageiros) { this.qtdPassageiros = qtdPassageiros; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
