package com.frota.service;

import org.springframework.stereotype.Service;
import com.frota.model.*;
import com.frota.repository.*;

@Service
public class TelemetriaService {

    private final TelemetriaRepository telemetriaRepo;
    private final MaquinaRepository maqRepo;

    public TelemetriaService(TelemetriaRepository t, MaquinaRepository m) {
        this.telemetriaRepo = t;
        this.maqRepo = m;
    }

    public Telemetria registrarTelemetria(Telemetria t) {
        Telemetria salva = telemetriaRepo.criar(t);
        maqRepo.atualizarHorasUso(t.getMaquinaId(), t.getHorasUso());
        return salva;
    }
}
