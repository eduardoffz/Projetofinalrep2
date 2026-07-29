package com.frota.repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.stereotype.Repository;
import com.frota.model.Maquina;

@Repository
public class MaquinaRepository {
    public Maquina criar(Maquina m) {
        String sql = "INSERT INTO maquinas (proprietario_id,nome,modelo,fabricante,ano_fabricacao,tipo,horas_uso_totais,preco_diaria,caucao,localizacao,disponivel,descricao,imagem_url,criado_em) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setLong(1, m.getProprietarioId()); p.setString(2, m.getNome()); p.setString(3, m.getModelo());
            p.setString(4, m.getFabricante()); p.setObject(5, m.getAnoFabricacao()); p.setString(6, m.getTipo());
            p.setDouble(7, m.getHorasUsoTotais()!=null?m.getHorasUsoTotais():0);
            p.setBigDecimal(8, m.getPrecoDiaria()); p.setBigDecimal(9, m.getCaucao());
            p.setString(10, m.getLocalizacao()); p.setBoolean(11, m.getDisponivel()!=null?m.getDisponivel():true);
            p.setString(12, m.getDescricao()); p.setString(13, m.getImagemUrl());
            p.setObject(14, LocalDateTime.now());
            p.executeUpdate();
            try (ResultSet r = p.getGeneratedKeys()) { if (r.next()) m.setId(r.getLong(1)); }
            m.setCriadoEm(LocalDateTime.now());
            return m;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Maquina> listarTodos() {
        List<Maquina> l = new ArrayList<>();
        String sql = "SELECT m.*, u.nome as proprietario_nome FROM maquinas m LEFT JOIN proprietarios p ON m.proprietario_id = p.id LEFT JOIN usuarios u ON p.usuario_id = u.id ORDER BY m.nome";
        try (Connection c = Conexao.getConnection(); Statement s = c.createStatement(); ResultSet r = s.executeQuery(sql)) {
            while (r.next()) l.add(mapear(r));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return l;
    }

    public List<Maquina> listarDisponiveis() {
        List<Maquina> l = new ArrayList<>();
        String sql = "SELECT m.*, u.nome as proprietario_nome FROM maquinas m LEFT JOIN proprietarios p ON m.proprietario_id = p.id LEFT JOIN usuarios u ON p.usuario_id = u.id WHERE m.disponivel = true ORDER BY m.nome";
        try (Connection c = Conexao.getConnection(); Statement s = c.createStatement(); ResultSet r = s.executeQuery(sql)) {
            while (r.next()) l.add(mapear(r));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return l;
    }

    public List<Maquina> listarPorProprietario(Long proprietarioId) {
        List<Maquina> l = new ArrayList<>();
        String sql = "SELECT m.*, u.nome as proprietario_nome FROM maquinas m LEFT JOIN proprietarios p ON m.proprietario_id = p.id LEFT JOIN usuarios u ON p.usuario_id = u.id WHERE m.proprietario_id = ? ORDER BY m.nome";
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, proprietarioId);
            try (ResultSet r = p.executeQuery()) { while (r.next()) l.add(mapear(r)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return l;
    }

    public List<Maquina> listarDestaques(int limite) {
        List<Maquina> l = new ArrayList<>();
        String sql = "SELECT m.*, u.nome as proprietario_nome FROM maquinas m LEFT JOIN proprietarios p ON m.proprietario_id = p.id LEFT JOIN usuarios u ON p.usuario_id = u.id WHERE m.disponivel = true ORDER BY m.criado_em DESC LIMIT ?";
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, limite);
            try (ResultSet r = p.executeQuery()) { while (r.next()) l.add(mapear(r)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return l;
    }

    public Optional<Maquina> buscarPorId(Long id) {
        String sql = "SELECT m.*, u.nome as proprietario_nome FROM maquinas m LEFT JOIN proprietarios p ON m.proprietario_id = p.id LEFT JOIN usuarios u ON p.usuario_id = u.id WHERE m.id=?";
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) { if (r.next()) return Optional.of(mapear(r)); }
            return Optional.empty();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void atualizarDisponibilidade(Long id, Boolean disponivel) {
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement("UPDATE maquinas SET disponivel=? WHERE id=?")) {
            p.setBoolean(1, disponivel); p.setLong(2, id); p.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void atualizarHorasUso(Long id, Double h) {
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement("UPDATE maquinas SET horas_uso_totais=?, ultima_telemetria=? WHERE id=?")) {
            p.setDouble(1, h); p.setObject(2, LocalDateTime.now()); p.setLong(3, id);
            p.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public long contar() {
        try (Connection c = Conexao.getConnection(); Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT COUNT(*) FROM maquinas")) {
            return r.next() ? r.getLong(1) : 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public long contarDisponiveis() {
        try (Connection c = Conexao.getConnection(); Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT COUNT(*) FROM maquinas WHERE disponivel=true")) {
            return r.next() ? r.getLong(1) : 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private Maquina mapear(ResultSet r) throws SQLException {
        Maquina m = new Maquina();
        m.setId(r.getLong("id")); m.setProprietarioId(r.getLong("proprietario_id"));
        m.setProprietarioNome(r.getString("proprietario_nome"));
        m.setNome(r.getString("nome")); m.setModelo(r.getString("modelo"));
        m.setFabricante(r.getString("fabricante")); m.setAnoFabricacao(r.getObject("ano_fabricacao",Integer.class));
        m.setTipo(r.getString("tipo")); m.setHorasUsoTotais(r.getObject("horas_uso_totais",Double.class));
        m.setPrecoDiaria(r.getBigDecimal("preco_diaria")); m.setCaucao(r.getBigDecimal("caucao"));
        m.setLocalizacao(r.getString("localizacao")); m.setDisponivel(r.getBoolean("disponivel"));
        m.setDescricao(r.getString("descricao")); m.setImagemUrl(r.getString("imagem_url"));
        m.setUltimaTelemetria(r.getObject("ultima_telemetria",LocalDateTime.class));
        m.setCriadoEm(r.getObject("criado_em",LocalDateTime.class));
        return m;
    }
}
