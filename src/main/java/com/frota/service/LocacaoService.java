package com.frota.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.stereotype.Service;
import com.frota.model.*;
import com.frota.repository.*;

@Service
public class LocacaoService {

    private final LocacaoRepository locacaoRepo;
    private final MaquinaRepository maquinaRepo;
    private final ServicoAdicionalRepository servicoRepo;
    private final LocacaoServicoRepository locacaoServicoRepo;
    private final IndisponibilidadeRepository indispRepo;

    public LocacaoService(LocacaoRepository lr, MaquinaRepository mr, ServicoAdicionalRepository sr,
                          LocacaoServicoRepository lsr, IndisponibilidadeRepository ir) {
        this.locacaoRepo = lr; this.maquinaRepo = mr; this.servicoRepo = sr;
        this.locacaoServicoRepo = lsr; this.indispRepo = ir;
    }

    public Locacao criarLocacao(LocacaoRequest dto, Long clienteId) {
        Maquina maq = maquinaRepo.buscarPorId(dto.getMaquinaId())
                .orElseThrow(() -> new RuntimeException("Maquina nao encontrada"));

        if (!maq.getDisponivel()) throw new RuntimeException("Maquina nao disponivel para locacao");

        if (dto.getDataInicio().isBefore(LocalDate.now())) throw new RuntimeException("Data de inicio nao pode ser no passado");
        if (dto.getDataFim().isBefore(dto.getDataInicio())) throw new RuntimeException("Data de fim deve ser posterior a data de inicio");

        if (locacaoRepo.existeConflitoDatas(maq.getId(), dto.getDataInicio(), dto.getDataFim()))
            throw new RuntimeException("Ja existe uma locacao neste periodo");

        if (indispRepo.existeConflito(maq.getId(), dto.getDataInicio(), dto.getDataFim()))
            throw new RuntimeException("A maquina esta indisponivel neste periodo");

        long dias = ChronoUnit.DAYS.between(dto.getDataInicio(), dto.getDataFim()) + 1;
        BigDecimal valorDiaria = maq.getPrecoDiaria();
        BigDecimal totalLocacao = valorDiaria.multiply(BigDecimal.valueOf(dias));

        BigDecimal totalServicos = BigDecimal.ZERO;
        if (dto.getServicoIds() != null) {
            for (Long servId : dto.getServicoIds()) {
                ServicoAdicional serv = servicoRepo.buscarPorId(servId)
                        .orElseThrow(() -> new RuntimeException("Servico nao encontrado: " + servId));
                totalServicos = totalServicos.add(serv.getPreco());
            }
        }

        BigDecimal valorTotal = totalLocacao.add(totalServicos).add(maq.getCaucao() != null ? maq.getCaucao() : BigDecimal.ZERO);

        Locacao loc = new Locacao();
        loc.setMaquinaId(maq.getId());
        loc.setClienteId(clienteId);
        loc.setDataInicio(dto.getDataInicio());
        loc.setDataFim(dto.getDataFim());
        loc.setStatus("PENDENTE");
        loc.setValorDiaria(valorDiaria);
        loc.setValorCaucao(maq.getCaucao());
        loc.setValorTotal(valorTotal);
        loc.setObservacoes(dto.getObservacoes());

        Locacao criada = locacaoRepo.criar(loc);

        if (dto.getServicoIds() != null) {
            for (Long servId : dto.getServicoIds()) {
                ServicoAdicional serv = servicoRepo.buscarPorId(servId).get();
                LocacaoServico ls = new LocacaoServico();
                ls.setLocacaoId(criada.getId());
                ls.setServicoId(servId);
                ls.setPrecoCobrado(serv.getPreco());
                locacaoServicoRepo.criar(ls);
            }
        }

        return locacaoRepo.buscarPorId(criada.getId()).orElse(criada);
    }

    public void aprovarLocacao(Long locacaoId) {
        Locacao loc = locacaoRepo.buscarPorId(locacaoId)
                .orElseThrow(() -> new RuntimeException("Locacao nao encontrada"));
        if (!"PENDENTE".equals(loc.getStatus()))
            throw new RuntimeException("Apenas locacoes pendentes podem ser aprovadas");
        locacaoRepo.atualizarStatus(locacaoId, "ATIVA");
        maquinaRepo.atualizarDisponibilidade(loc.getMaquinaId(), false);
    }

    public void concluirLocacao(Long locacaoId) {
        Locacao loc = locacaoRepo.buscarPorId(locacaoId)
                .orElseThrow(() -> new RuntimeException("Locacao nao encontrada"));
        if (!"ATIVA".equals(loc.getStatus()) && !"ATRASADA".equals(loc.getStatus()))
            throw new RuntimeException("Apenas locacoes ativas podem ser concluidas");
        locacaoRepo.atualizarDataDevolucao(locacaoId, LocalDate.now());
        maquinaRepo.atualizarDisponibilidade(loc.getMaquinaId(), true);
    }

    public void cancelarLocacao(Long locacaoId) {
        Locacao loc = locacaoRepo.buscarPorId(locacaoId)
                .orElseThrow(() -> new RuntimeException("Locacao nao encontrada"));
        if ("CONCLUIDA".equals(loc.getStatus()) || "CANCELADA".equals(loc.getStatus()))
            throw new RuntimeException("Locacao ja foi " + loc.getStatus().toLowerCase());
        locacaoRepo.atualizarStatus(locacaoId, "CANCELADA");
        maquinaRepo.atualizarDisponibilidade(loc.getMaquinaId(), true);
    }

    public List<Locacao> listarLocacoesCliente(Long clienteId) {
        return locacaoRepo.listarPorCliente(clienteId);
    }

    public List<Locacao> listarLocacoesProprietario(Long proprietarioId) {
        return locacaoRepo.listarPorProprietario(proprietarioId);
    }

    public List<Locacao> listarAtivas() {
        return locacaoRepo.listarAtivas();
    }

    public Locacao buscarDetalhes(Long id) {
        return locacaoRepo.buscarPorId(id).orElseThrow(() -> new RuntimeException("Locacao nao encontrada"));
    }

    public long contarAtivas() { return locacaoRepo.contarAtivas(); }
}
