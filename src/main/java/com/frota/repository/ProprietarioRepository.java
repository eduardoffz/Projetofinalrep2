package com.frota.repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.stereotype.Repository;
import com.frota.model.Proprietario;

@Repository
public class ProprietarioRepository {
    public Proprietario criar(Proprietario p) {
        String sql = "INSERT INTO proprietarios (usuario_id,documento,chave_pix,endereco,criado_em) VALUES (?,?,?,?,?)";
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, p.getUsuarioId()); ps.setString(2, p.getDocumento());
            ps.setString(3, p.getChavePix()); ps.setString(4, p.getEndereco());
            ps.setObject(5, LocalDateTime.now());
            ps.executeUpdate();
            try (ResultSet r = ps.getGeneratedKeys()) { if (r.next()) p.setId(r.getLong(1)); }
            p.setCriadoEm(LocalDateTime.now());
            return p;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Optional<Proprietario> buscarPorId(Long id) {
        String sql = "SELECT p.*, u.nome, u.email, u.telefone FROM proprietarios p LEFT JOIN usuarios u ON p.usuario_id = u.id WHERE p.id=?";
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet r = ps.executeQuery()) { if (r.next()) return Optional.of(mapear(r)); }
            return Optional.empty();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Optional<Proprietario> buscarPorUsuarioId(Long usuarioId) {
        String sql = "SELECT p.*, u.nome, u.email, u.telefone FROM proprietarios p LEFT JOIN usuarios u ON p.usuario_id = u.id WHERE p.usuario_id=?";
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, usuarioId);
            try (ResultSet r = ps.executeQuery()) { if (r.next()) return Optional.of(mapear(r)); }
            return Optional.empty();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Proprietario> listarTodos() {
        List<Proprietario> l = new ArrayList<>();
        String sql = "SELECT p.*, u.nome, u.email, u.telefone FROM proprietarios p LEFT JOIN usuarios u ON p.usuario_id = u.id ORDER BY u.nome";
        try (Connection c = Conexao.getConnection(); Statement s = c.createStatement(); ResultSet r = s.executeQuery(sql)) {
            while (r.next()) l.add(mapear(r));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return l;
    }

    public long contar() {
        try (Connection c = Conexao.getConnection(); Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT COUNT(*) FROM proprietarios")) {
            return r.next() ? r.getLong(1) : 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private Proprietario mapear(ResultSet r) throws SQLException {
        Proprietario p = new Proprietario();
        p.setId(r.getLong("id")); p.setUsuarioId(r.getLong("usuario_id"));
        p.setNome(r.getString("nome")); p.setEmail(r.getString("email"));
        p.setTelefone(r.getString("telefone"));
        p.setDocumento(r.getString("documento")); p.setChavePix(r.getString("chave_pix"));
        p.setEndereco(r.getString("endereco")); p.setRating(r.getObject("rating", Double.class));
        p.setCriadoEm(r.getObject("criado_em", LocalDateTime.class));
        return p;
    }
}
