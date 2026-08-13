package com.frota.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Maquina {
    private Long id;
    private Long proprietarioId;
    private String proprietarioNome;
    private String nome;
    private String modelo;
    private String fabricante;
    private Integer anoFabricacao;
    private String tipo;
    private Double horasUsoTotais;
    private BigDecimal precoDiaria;
    private BigDecimal caucao;
    private String localizacao;
    private Boolean disponivel;
    private String descricao;
    private String imagemUrl;
    private LocalDateTime ultimaTelemetria;
    private LocalDateTime criadoEm;

    public Maquina() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProprietarioId() { return proprietarioId; }
    public void setProprietarioId(Long proprietarioId) { this.proprietarioId = proprietarioId; }
    public String getProprietarioNome() { return proprietarioNome; }
    public void setProprietarioNome(String proprietarioNome) { this.proprietarioNome = proprietarioNome; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public String getFabricante() { return fabricante; }
    public void setFabricante(String fabricante) { this.fabricante = fabricante; }
    public Integer getAnoFabricacao() { return anoFabricacao; }
    public void setAnoFabricacao(Integer anoFabricacao) { this.anoFabricacao = anoFabricacao; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Double getHorasUsoTotais() { return horasUsoTotais; }
    public void setHorasUsoTotais(Double horasUsoTotais) { this.horasUsoTotais = horasUsoTotais; }
    public BigDecimal getPrecoDiaria() { return precoDiaria; }
    public void setPrecoDiaria(BigDecimal precoDiaria) { this.precoDiaria = precoDiaria; }
    public BigDecimal getCaucao() { return caucao; }
    public void setCaucao(BigDecimal caucao) { this.caucao = caucao; }
    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }
    public Boolean getDisponivel() { return disponivel; }
    public void setDisponivel(Boolean disponivel) { this.disponivel = disponivel; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getImagemUrl() { return imagemUrl; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }
    public LocalDateTime getUltimaTelemetria() { return ultimaTelemetria; }
    public void setUltimaTelemetria(LocalDateTime ultimaTelemetria) { this.ultimaTelemetria = ultimaTelemetria; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
}
