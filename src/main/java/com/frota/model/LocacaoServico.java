package com.frota.model;

import java.math.BigDecimal;

public class LocacaoServico {
    private Long id;
    private Long locacaoId;
    private Long servicoId;
    private String servicoNome;
    private BigDecimal precoCobrado;

    public LocacaoServico() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getLocacaoId() { return locacaoId; }
    public void setLocacaoId(Long locacaoId) { this.locacaoId = locacaoId; }
    public Long getServicoId() { return servicoId; }
    public void setServicoId(Long servicoId) { this.servicoId = servicoId; }
    public String getServicoNome() { return servicoNome; }
    public void setServicoNome(String servicoNome) { this.servicoNome = servicoNome; }
    public BigDecimal getPrecoCobrado() { return precoCobrado; }
    public void setPrecoCobrado(BigDecimal precoCobrado) { this.precoCobrado = precoCobrado; }
}
