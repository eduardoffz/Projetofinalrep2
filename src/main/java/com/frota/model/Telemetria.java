package com.frota.model;

import java.time.LocalDateTime;

public class Telemetria {
    private Long id;
    private Long maquinaId;
    private Long locacaoId;
    private Double horasUso;
    private Double temperaturaMotor;
    private Double consumoCombustivel;
    private Double rpmMotor;
    private Double pressaoOleo;
    private LocalDateTime dataRegistro;

    public Telemetria() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMaquinaId() { return maquinaId; }
    public void setMaquinaId(Long maquinaId) { this.maquinaId = maquinaId; }
    public Long getLocacaoId() { return locacaoId; }
    public void setLocacaoId(Long locacaoId) { this.locacaoId = locacaoId; }
    public Double getHorasUso() { return horasUso; }
    public void setHorasUso(Double horasUso) { this.horasUso = horasUso; }
    public Double getTemperaturaMotor() { return temperaturaMotor; }
    public void setTemperaturaMotor(Double temperaturaMotor) { this.temperaturaMotor = temperaturaMotor; }
    public Double getConsumoCombustivel() { return consumoCombustivel; }
    public void setConsumoCombustivel(Double consumoCombustivel) { this.consumoCombustivel = consumoCombustivel; }
    public Double getRpmMotor() { return rpmMotor; }
    public void setRpmMotor(Double rpmMotor) { this.rpmMotor = rpmMotor; }
    public Double getPressaoOleo() { return pressaoOleo; }
    public void setPressaoOleo(Double pressaoOleo) { this.pressaoOleo = pressaoOleo; }
    public LocalDateTime getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDateTime dataRegistro) { this.dataRegistro = dataRegistro; }
}
