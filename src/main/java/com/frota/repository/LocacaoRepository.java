package com.frota.repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.stereotype.Repository;
import com.frota.model.Locacao;

@Repository
public class LocacaoRepository {
    public Locacao criar(Locacao l) {
        String sql = "INSERT INTO locacoes (maquina_id,cliente_id,data_inicio,data_fim,status,valor_diaria,valor_caucao,valor_total,observacoes,created_at) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setLong(1, l.getMaquinaId()); p.setLong(2, l.getClienteId());
            p.setObject(3, l.getDataInicio()); p.setObject(4, l.getDataFim());
            p.setString(5, l.getStatus() != null ? l.getStatus() : "PENDENTE");
            p.setBigDecimal(6, l.getValorDiaria()); p.setBigDecimal(7, l.getValorCaucao());
            p.setBigDecimal(8, l.getValorTotal()); p.setString(9, l.getObservacoes());
            p.setObject(10, LocalDateTime.now());
            p.executeUpdate();
            try (ResultSet r = p.getGeneratedKeys()) { if (r.next()) l.setId(r.getLong(1)); }
            l.setCreatedAt(LocalDateTime.now());
            return l;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Locacao> listarTodos() {
        List<Locacao> l = new ArrayList<>();
        String sql = "SELECT l.*, u.nome as cliente_nome, m.nome as maquina_nome, m.tipo as maquina_tipo " +
                     "FROM locacoes l " +
                     "LEFT JOIN usuarios u ON l.cliente_id = u.id " +
                     "LEFT JOIN maquinas m ON l.maquina_id = m.id " +
                     "ORDER BY l.created_at DESC";
        try (Connection c = Conexao.getConnection(); Statement s = c.createStatement(); ResultSet r = s.executeQuery(sql)) {
            while (r.next()) l.add(mapear(r));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return l;
    }

    public List<Locacao> listarPorCliente(Long clienteId) {
        List<Locacao> l = new ArrayList<>();
        String sql = "SELECT l.*, u.nome as cliente_nome, m.nome as maquina_nome, m.tipo as maquina_tipo " +
                     "FROM locacoes l " +
                     "LEFT JOIN usuarios u ON l.cliente_id = u.id " +
                     "LEFT JOIN maquinas m ON l.maquina_id = m.id " +
                     "WHERE l.cliente_id = ? ORDER BY l.created_at DESC";
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, clienteId);
            try (ResultSet r = p.executeQuery()) { while (r.next()) l.add(mapear(r)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return l;
    }

    public List<Locacao> listarPorProprietario(Long proprietarioId) {
        List<Locacao> l = new ArrayList<>();
        String sql = "SELECT l.*, u.nome as cliente_nome, m.nome as maquina_nome, m.tipo as maquina_tipo " +
                     "FROM locacoes l " +
                     "LEFT JOIN usuarios u ON l.cliente_id = u.id " +
                     "LEFT JOIN maquinas m ON l.maquina_id = m.id " +
                     "WHERE m.proprietario_id = ? ORDER BY l.created_at DESC";
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, proprietarioId);
            try (ResultSet r = p.executeQuery()) { while (r.next()) l.add(mapear(r)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return l;
    }

    public List<Locacao> listarAtivas() {
        List<Locacao> l = new ArrayList<>();
        String sql = "SELECT l.*, u.nome as cliente_nome, m.nome as maquina_nome, m.tipo as maquina_tipo " +
                     "FROM locacoes l " +
                     "LEFT JOIN usuarios u ON l.cliente_id = u.id " +
                     "LEFT JOIN maquinas m ON l.maquina_id = m.id " +
                     "WHERE l.status IN ('PENDENTE','ATIVA','ATRASADA') ORDER BY l.data_inicio";
        try (Connection c = Conexao.getConnection(); Statement s = c.createStatement(); ResultSet r = s.executeQuery(sql)) {
            while (r.next()) l.add(mapear(r));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return l;
    }

    public Optional<Locacao> buscarPorId(Long id) {
        String sql = "SELECT l.*, u.nome as cliente_nome, m.nome as maquina_nome, m.tipo as maquina_tipo " +
                     "FROM locacoes l " +
                     "LEFT JOIN usuarios u ON l.cliente_id = u.id " +
                     "LEFT JOIN maquinas m ON l.maquina_id = m.id " +
                     "WHERE l.id=?";
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) { if (r.next()) return Optional.of(mapear(r)); }
            return Optional.empty();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public boolean existeConflitoDatas(Long maquinaId, LocalDate inicio, LocalDate fim) {
        String sql = "SELECT COUNT(*) FROM locacoes WHERE maquina_id = ? AND status IN ('PENDENTE','ATIVA') AND data_inicio < ? AND data_fim > ?";
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, maquinaId); p.setObject(2, fim); p.setObject(3, inicio);
            try (ResultSet r = p.executeQuery()) { return r.next() && r.getLong(1) > 0; }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void atualizarStatus(Long id, String status) {
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement("UPDATE locacoes SET status=?, updated_at=? WHERE id=?")) {
            p.setString(1, status); p.setObject(2, LocalDateTime.now()); p.setLong(3, id); p.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void atualizarDataDevolucao(Long id, LocalDate dataDevolucao) {
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement("UPDATE locacoes SET data_devolucao=?, status='CONCLUIDA', updated_at=? WHERE id=?")) {
            p.setObject(1, dataDevolucao); p.setObject(2, LocalDateTime.now()); p.setLong(3, id); p.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public long contarAtivas() {
        try (Connection c = Conexao.getConnection(); Statement s = c.createStatement();
             ResultSet r = s.executeQuery("SELECT COUNT(*) FROM locacoes WHERE status IN ('PENDENTE','ATIVA','ATRASADA')")) {
            return r.next() ? r.getLong(1) : 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Map<String, Long> contarPorStatus() {
        Map<String, Long> map = new HashMap<>();
        try (Connection c = Conexao.getConnection(); Statement s = c.createStatement();
             ResultSet r = s.executeQuery("SELECT status, COUNT(*) as total FROM locacoes GROUP BY status")) {
            while (r.next()) map.put(r.getString("status").toLowerCase(), r.getLong("total"));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return map;
    }

    public Map<String, Long> contarPorTipoMaquina() {
        Map<String, Long> map = new HashMap<>();
        String sql = "SELECT m.tipo, COUNT(*) as total FROM locacoes l JOIN maquinas m ON l.maquina_id = m.id GROUP BY m.tipo";
        try (Connection c = Conexao.getConnection(); Statement s = c.createStatement(); ResultSet r = s.executeQuery(sql)) {
            while (r.next()) map.put(r.getString("tipo").toLowerCase(), r.getLong("total"));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return map;
    }

    public List<Locacao> listarRecentes(int limite) {
        List<Locacao> l = new ArrayList<>();
        String sql = "SELECT l.*, u.nome as cliente_nome, m.nome as maquina_nome, m.tipo as maquina_tipo " +
                     "FROM locacoes l " +
                     "LEFT JOIN usuarios u ON l.cliente_id = u.id " +
                     "LEFT JOIN maquinas m ON l.maquina_id = m.id " +
                     "ORDER BY l.created_at DESC LIMIT ?";
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, limite);
            try (ResultSet r = p.executeQuery()) { while (r.next()) l.add(mapear(r)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return l;
    }

    public long contarPorCliente(Long clienteId) {
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement("SELECT COUNT(*) FROM locacoes WHERE cliente_id=?")) {
            p.setLong(1, clienteId);
            try (ResultSet r = p.executeQuery()) { return r.next() ? r.getLong(1) : 0; }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private Locacao mapear(ResultSet r) throws SQLException {
        Locacao l = new Locacao();
        l.setId(r.getLong("id")); l.setMaquinaId(r.getLong("maquina_id"));
        l.setClienteId(r.getLong("cliente_id"));
        l.setClienteNome(r.getString("cliente_nome"));
        l.setMaquinaNome(r.getString("maquina_nome"));
        l.setMaquinaTipo(r.getString("maquina_tipo"));
        l.setDataInicio(r.getObject("data_inicio", LocalDate.class));
        l.setDataFim(r.getObject("data_fim", LocalDate.class));
        l.setDataDevolucao(r.getObject("data_devolucao", LocalDate.class));
        l.setStatus(r.getString("status"));
        l.setValorDiaria(r.getBigDecimal("valor_diaria"));
        l.setValorCaucao(r.getBigDecimal("valor_caucao"));
        l.setValorTotal(r.getBigDecimal("valor_total"));
        l.setObservacoes(r.getString("observacoes"));
        l.setCreatedAt(r.getObject("created_at", LocalDateTime.class));
        l.setUpdatedAt(r.getObject("updated_at", LocalDateTime.class));
        return l;
    }
}
