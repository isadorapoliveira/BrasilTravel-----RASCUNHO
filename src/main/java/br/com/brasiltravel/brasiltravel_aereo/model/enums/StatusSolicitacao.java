package br.com.brasiltravel.brasiltravel_aereo.model.enums;

public enum StatusSolicitacao {
    AGUARDANDO_ATENDIMENTO("Aguardando atendimento"),
    EM_ATENDIMENTO("Em atendimento"),
    PENDENTE_CLIENTE("Pendente cliente"),
    CONFIRMADA("Confirmada"),
    CANCELADA("Cancelada"),
    FINALIZADA("Finalizada");

    private final String descricao;

    StatusSolicitacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
