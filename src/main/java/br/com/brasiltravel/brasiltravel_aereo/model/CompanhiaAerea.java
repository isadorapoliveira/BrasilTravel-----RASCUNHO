package br.com.brasiltravel.brasiltravel_aereo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "companhias_aereas")
public class CompanhiaAerea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_companhia")
    private Long idCompanhia;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String nome;

    @NotBlank
    @Column(name = "codigo_iata", nullable = false, unique = true, length = 3)
    private String codigoIata;

    @Column(length = 160)
    private String site;

    @Column(length = 25)
    private String telefone;

    @Column(nullable = false)
    private Boolean ativo = true;

    public CompanhiaAerea() {
    }

    public CompanhiaAerea(String nome, String codigoIata, String site, String telefone, Boolean ativo) {
        this.nome = nome;
        this.codigoIata = codigoIata;
        this.site = site;
        this.telefone = telefone;
        this.ativo = ativo;
    }

    public Long getIdCompanhia() {
        return idCompanhia;
    }

    public void setIdCompanhia(Long idCompanhia) {
        this.idCompanhia = idCompanhia;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigoIata() {
        return codigoIata;
    }

    public void setCodigoIata(String codigoIata) {
        this.codigoIata = codigoIata;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
