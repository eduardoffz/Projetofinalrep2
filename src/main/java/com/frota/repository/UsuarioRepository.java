package com.frota.repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.stereotype.Repository;
import com.frota.model.Usuario;

@Repository
public class UsuarioRepository {
    public Usuario criar(Usuario u) {
        String sql = "INSERT INTO usuarios (nome,email,senha,role,telefone,criado_em) VALUES (?,?,?,?,?,?)";
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, u.getNome()); p.setString(2, u.getEmail());
            p.setString(3, u.getSenha()); p.setString(4, u.getRole());
            p.setString(5, u.getTelefone()); p.setObject(6, LocalDateTime.now());
            p.executeUpdate();
            try (ResultSet r = p.getGeneratedKeys()) { if (r.next()) u.setId(r.getLong(1)); }
            u.setCriadoEm(LocalDateTime.now());
            return u;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement("SELECT * FROM usuarios WHERE email=?")) {
            p.setString(1, email);
            try (ResultSet r = p.executeQuery()) { if (r.next()) return Optional.of(mapear(r)); }
            return Optional.empty();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Optional<Usuario> buscarPorId(Long id) {
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement("SELECT * FROM usuarios WHERE id=?")) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) { if (r.next()) return Optional.of(mapear(r)); }
            return Optional.empty();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Usuario> listarPorRole(String role) {
        List<Usuario> l = new ArrayList<>();
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement("SELECT * FROM usuarios WHERE role=? ORDER BY nome")) {
            p.setString(1, role);
            try (ResultSet r = p.executeQuery()) { while (r.next()) l.add(mapear(r)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return l;
    }

    public long contarPorRole(String role) {
        try (Connection c = Conexao.getConnection(); PreparedStatement p = c.prepareStatement("SELECT COUNT(*) FROM usuarios WHERE role=?")) {
            p.setString(1, role);
            try (ResultSet r = p.executeQuery()) { return r.next() ? r.getLong(1) : 0; }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private Usuario mapear(ResultSet r) throws SQLException {
        Usuario u = new Usuario();
        u.setId(r.getLong("id")); u.setNome(r.getString("nome")); u.setEmail(r.getString("email"));
        u.setSenha(r.getString("senha")); u.setRole(r.getString("role"));
        u.setTelefone(r.getString("telefone"));
        u.setCriadoEm(r.getObject("criado_em", LocalDateTime.class));
        return u;
    }
}
