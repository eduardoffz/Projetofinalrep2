package com.frota.repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import org.springframework.stereotype.Repository;
import com.frota.model.Indisponibilidade;

@Repository
public class IndisponibilidadeRepository {
    public Indisponibilidade criar(Indisponibilidade i) {
        String sql = "INSERT INTO indisponibilidades (maquina_id,data_inicio,data_fim,motivo) VALUES (?,?,?,?)";
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setLong(1, i.getMaquinaId()); p.setObject(2, i.getDataInicio());
            p.setObject(3, i.getDataFim()); p.setString(4, i.getMotivo());
            p.executeUpdate();
            try (ResultSet r = p.getGeneratedKeys()) { if (r.next()) i.setId(r.getLong(1)); }
            return i;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Indisponibilidade> listarPorMaquina(Long maquinaId) {
        List<Indisponibilidade> l = new ArrayList<>();
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement("SELECT * FROM indisponibilidades WHERE maquina_id=? ORDER BY data_inicio")) {
            p.setLong(1, maquinaId);
            try (ResultSet r = p.executeQuery()) { while (r.next()) l.add(mapear(r)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return l;
    }

    public boolean existeConflito(Long maquinaId, LocalDate inicio, LocalDate fim) {
        String sql = "SELECT COUNT(*) FROM indisponibilidades WHERE maquina_id=? AND data_inicio < ? AND data_fim > ?";
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, maquinaId); p.setObject(2, fim); p.setObject(3, inicio);
            try (ResultSet r = p.executeQuery()) { return r.next() && r.getLong(1) > 0; }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private Indisponibilidade mapear(ResultSet r) throws SQLException {
        Indisponibilidade i = new Indisponibilidade();
        i.setId(r.getLong("id")); i.setMaquinaId(r.getLong("maquina_id"));
        i.setDataInicio(r.getObject("data_inicio", LocalDate.class));
        i.setDataFim(r.getObject("data_fim", LocalDate.class));
        i.setMotivo(r.getString("motivo"));
        return i;
    }
}
