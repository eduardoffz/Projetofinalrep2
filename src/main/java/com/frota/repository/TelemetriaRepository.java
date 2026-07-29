package com.frota.repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.stereotype.Repository;
import com.frota.model.Telemetria;

@Repository
public class TelemetriaRepository {
    public Telemetria criar(Telemetria t) {
        String sql = "INSERT INTO telemetria (maquina_id,locacao_id,horas_uso,temperatura_motor,consumo_combustivel,rpm_motor,pressao_oleo,data_registro) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setLong(1,t.getMaquinaId()); p.setObject(2,t.getLocacaoId());
            p.setDouble(3,t.getHorasUso()); p.setDouble(4,t.getTemperaturaMotor());
            p.setDouble(5,t.getConsumoCombustivel()); p.setDouble(6,t.getRpmMotor()); p.setDouble(7,t.getPressaoOleo());
            p.setObject(8, LocalDateTime.now());
            p.executeUpdate();
            try (ResultSet r = p.getGeneratedKeys()) { if (r.next()) t.setId(r.getLong(1)); }
            t.setDataRegistro(LocalDateTime.now());
            return t;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Telemetria> listarPorMaquina(Long id) {
        List<Telemetria> l = new ArrayList<>();
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement("SELECT * FROM telemetria WHERE maquina_id=? ORDER BY data_registro DESC")) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) { while (r.next()) l.add(mapear(r)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return l;
    }

    public List<Telemetria> listarPorLocacao(Long locacaoId) {
        List<Telemetria> l = new ArrayList<>();
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement("SELECT * FROM telemetria WHERE locacao_id=? ORDER BY data_registro DESC")) {
            p.setLong(1, locacaoId);
            try (ResultSet r = p.executeQuery()) { while (r.next()) l.add(mapear(r)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return l;
    }

    private Telemetria mapear(ResultSet r) throws SQLException {
        Telemetria t = new Telemetria();
        t.setId(r.getLong("id")); t.setMaquinaId(r.getLong("maquina_id"));
        t.setLocacaoId(r.getObject("locacao_id",Long.class));
        t.setHorasUso(r.getDouble("horas_uso")); t.setTemperaturaMotor(r.getDouble("temperatura_motor"));
        t.setConsumoCombustivel(r.getDouble("consumo_combustivel")); t.setRpmMotor(r.getDouble("rpm_motor"));
        t.setPressaoOleo(r.getDouble("pressao_oleo")); t.setDataRegistro(r.getObject("data_registro",LocalDateTime.class));
        return t;
    }
}
