package com.frota.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Locacao {
    private Long id;
    private Long maquinaId;
    private Long clienteId;
    private String clienteNome;
    private String maquinaNome;
    private String maquinaTipo;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private LocalDate dataDevolucao;
    private String status;
    private BigDecimal valorDiaria;
    private BigDecimal valorCaucao;
    private BigDecimal valorTotal;
    private String observacoes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ServicoAdicional> servicos;

    private String proprietarioNome;
    private String proprietarioEmail;
    private String proprietarioTelefone;
    private String proprietarioChavePix;

    public Locacao() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMaquinaId() { return maquinaId; }
    public void setMaquinaId(Long maquinaId) { this.maquinaId = maquinaId; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }
    public String getMaquinaNome() { return maquinaNome; }
    public void setMaquinaNome(String maquinaNome) { this.maquinaNome = maquinaNome; }
    public String getMaquinaTipo() { return maquinaTipo; }
    public void setMaquinaTipo(String maquinaTipo) { this.maquinaTipo = maquinaTipo; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
    public LocalDate getDataDevolucao() { return dataDevolucao; }
    public void setDataDevolucao(LocalDate dataDevolucao) { this.dataDevolucao = dataDevolucao; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getValorDiaria() { return valorDiaria; }
    public void setValorDiaria(BigDecimal valorDiaria) { this.valorDiaria = valorDiaria; }
    public BigDecimal getValorCaucao() { return valorCaucao; }
    public void setValorCaucao(BigDecimal valorCaucao) { this.valorCaucao = valorCaucao; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<ServicoAdicional> getServicos() { return servicos; }
    public void setServicos(List<ServicoAdicional> servicos) { this.servicos = servicos; }

    public String getProprietarioNome() { return proprietarioNome; }
    public void setProprietarioNome(String proprietarioNome) { this.proprietarioNome = proprietarioNome; }
    public String getProprietarioEmail() { return proprietarioEmail; }
    public void setProprietarioEmail(String proprietarioEmail) { this.proprietarioEmail = proprietarioEmail; }
    public String getProprietarioTelefone() { return proprietarioTelefone; }
    public void setProprietarioTelefone(String proprietarioTelefone) { this.proprietarioTelefone = proprietarioTelefone; }
    public String getProprietarioChavePix() { return proprietarioChavePix; }
    public void setProprietarioChavePix(String proprietarioChavePix) { this.proprietarioChavePix = proprietarioChavePix; }

    public String getChavePixValida() {
        if (proprietarioChavePix != null && !proprietarioChavePix.trim().isEmpty()) {
            return proprietarioChavePix.trim();
        }
        if (proprietarioEmail != null && !proprietarioEmail.trim().isEmpty()) {
            return proprietarioEmail.trim();
        }
        return "pix@agrirent.com.br";
    }

    public String getCodigoPix() {
        String key = getChavePixValida();
        String val = (valorTotal != null) ? String.format(java.util.Locale.US, "%.2f", valorTotal) : "0.00";
        String prop = (proprietarioNome != null) ? proprietarioNome : "AgriRent Proprietario";
        if (prop.length() > 20) prop = prop.substring(0, 20);
        return "00020126580014BR.GOV.BCB.PIX0136" + key + "520400005303986540" + String.format("%02d", val.length()) + val + "5802BR59" + String.format("%02d", prop.length()) + prop + "6009SAO PAULO62070503***6304E8A2";
    }
}
