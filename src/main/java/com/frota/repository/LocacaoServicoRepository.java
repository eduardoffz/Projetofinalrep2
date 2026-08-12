package com.frota.repository;

import java.sql.*;
import java.util.*;
import org.springframework.stereotype.Repository;
import com.frota.model.LocacaoServico;

@Repository
public class LocacaoServicoRepository {
    public LocacaoServico criar(LocacaoServico ls) {
        String sql = "INSERT INTO locacao_servicos (locacao_id,servico_id,preco_cobrado) VALUES (?,?,?)";
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setLong(1, ls.getLocacaoId()); p.setLong(2, ls.getServicoId());
            p.setBigDecimal(3, ls.getPrecoCobrado());
            p.executeUpdate();
            try (ResultSet r = p.getGeneratedKeys()) { if (r.next()) ls.setId(r.getLong(1)); }
            return ls;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<LocacaoServico> listarPorLocacao(Long locacaoId) {
        List<LocacaoServico> l = new ArrayList<>();
        String sql = "SELECT ls.*, s.nome as servico_nome FROM locacao_servicos ls LEFT JOIN servicos_adicionais s ON ls.servico_id = s.id WHERE ls.locacao_id=?";
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, locacaoId);
            try (ResultSet r = p.executeQuery()) { while (r.next()) l.add(mapear(r)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return l;
    }

    private LocacaoServico mapear(ResultSet r) throws SQLException {
        LocacaoServico ls = new LocacaoServico();
        ls.setId(r.getLong("id")); ls.setLocacaoId(r.getLong("locacao_id"));
        ls.setServicoId(r.getLong("servico_id"));
        ls.setServicoNome(r.getString("servico_nome"));
        ls.setPrecoCobrado(r.getBigDecimal("preco_cobrado"));
        return ls;
    }
}
