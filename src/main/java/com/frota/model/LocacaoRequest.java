package com.frota.model;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class LocacaoRequest {
    @NotNull
    private Long maquinaId;
    @NotNull
    private LocalDate dataInicio;
    @NotNull
    private LocalDate dataFim;
    private List<Long> servicoIds;
    private String observacoes;

    public Long getMaquinaId() { return maquinaId; }
    public void setMaquinaId(Long maquinaId) { this.maquinaId = maquinaId; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
    public List<Long> getServicoIds() { return servicoIds; }
    public void setServicoIds(List<Long> servicoIds) { this.servicoIds = servicoIds; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
