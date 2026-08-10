package com.frota.repository;

import java.sql.*;
import java.util.*;
import org.springframework.stereotype.Repository;
import com.frota.model.ServicoAdicional;

@Repository
public class ServicoAdicionalRepository {
    public ServicoAdicional criar(ServicoAdicional s) {
        String sql = "INSERT INTO servicos_adicionais (nome,descricao,preco,tipo,ativo) VALUES (?,?,?,?,?)";
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, s.getNome()); p.setString(2, s.getDescricao());
            p.setBigDecimal(3, s.getPreco()); p.setString(4, s.getTipo());
            p.setBoolean(5, s.getAtivo() != null ? s.getAtivo() : true);
            p.executeUpdate();
            try (ResultSet r = p.getGeneratedKeys()) { if (r.next()) s.setId(r.getLong(1)); }
            return s;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<ServicoAdicional> listarTodos() {
        List<ServicoAdicional> l = new ArrayList<>();
        try (Connection c = Conexao.getConnection(); Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT * FROM servicos_adicionais WHERE ativo=true ORDER BY nome")) {
            while (r.next()) l.add(mapear(r));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return l;
    }

    public Optional<ServicoAdicional> buscarPorId(Long id) {
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement("SELECT * FROM servicos_adicionais WHERE id=?")) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) { if (r.next()) return Optional.of(mapear(r)); }
            return Optional.empty();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private ServicoAdicional mapear(ResultSet r) throws SQLException {
        ServicoAdicional s = new ServicoAdicional();
        s.setId(r.getLong("id")); s.setNome(r.getString("nome"));
        s.setDescricao(r.getString("descricao")); s.setPreco(r.getBigDecimal("preco"));
        s.setTipo(r.getString("tipo")); s.setAtivo(r.getBoolean("ativo"));
        return s;
    }
}
