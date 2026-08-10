package com.frota.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.frota.model.*;
import com.frota.repository.*;

@Service
public class ProprietarioService {

    private final ProprietarioRepository propRepo;
    private final MaquinaRepository maqRepo;
    private final LocacaoRepository locRepo;

    public ProprietarioService(ProprietarioRepository pr, MaquinaRepository mr, LocacaoRepository lr) {
        this.propRepo = pr; this.maqRepo = mr; this.locRepo = lr;
    }

    public Proprietario registrar(Long usuarioId, String documento, String chavePix, String endereco) {
        Proprietario p = new Proprietario();
        p.setUsuarioId(usuarioId);
        p.setDocumento(documento);
        p.setChavePix(chavePix);
        p.setEndereco(endereco);
        return propRepo.criar(p);
    }

    public Proprietario buscarPorUsuario(Long usuarioId) {
        return propRepo.buscarPorUsuarioId(usuarioId).orElse(null);
    }

    public List<Maquina> listarMaquinas(Long proprietarioId) {
        return maqRepo.listarPorProprietario(proprietarioId);
    }

    public List<Locacao> listarLocacoes(Long proprietarioId) {
        return locRepo.listarPorProprietario(proprietarioId);
    }

    public long contarProprietarios() { return propRepo.contar(); }

    public List<Proprietario> listarTodos() { return propRepo.listarTodos(); }
}
