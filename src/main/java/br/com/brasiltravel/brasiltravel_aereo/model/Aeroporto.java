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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "aeroportos")
public class Aeroporto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aeroporto")
    private Long idAeroporto;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_destino", nullable = false)
    private Destino destino;

    @NotBlank
    @Column(name = "codigo_iata", nullable = false, unique = true, length = 3)
    private String codigoIata;

    @NotBlank
    @Column(nullable = false, length = 160)
    private String nome;

    @Column(length = 255)
    private String endereco;

    @Column(nullable = false)
    private Boolean ativo = true;

    public Aeroporto() {
    }

    public Aeroporto(Destino destino, String codigoIata, String nome, String endereco, Boolean ativo) {
        this.destino = destino;
        this.codigoIata = codigoIata;
        this.nome = nome;
        this.endereco = endereco;
        this.ativo = ativo;
    }

    public Long getIdAeroporto() {
        return idAeroporto;
    }

    public void setIdAeroporto(Long idAeroporto) {
        this.idAeroporto = idAeroporto;
    }

    public Destino getDestino() {
        return destino;
    }

    public void setDestino(Destino destino) {
        this.destino = destino;
    }

    public String getCodigoIata() {
        return codigoIata;
    }

    public void setCodigoIata(String codigoIata) {
        this.codigoIata = codigoIata;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getDescricaoCompleta() {
        return codigoIata + " - " + nome;
    }
}
