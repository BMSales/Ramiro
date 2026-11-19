package com.soft.developer;

import java.sql.*;
import java.math.BigDecimal;
import java.util.*;

public class ProdutoRepository {
    private final Banco banco;

    public ProdutoRepository() {
        this(new Banco());
    }

    public ProdutoRepository(Banco banco) {
        this.banco = banco;
    }

    private RuntimeException falha(SQLException e) {
        return new IllegalStateException("Falha no banco: " + e.getMessage(), e);
    }

    public void salvar(Produto p) {
        validar(p.getId(), p.getNome(), p.getPreco(), p.getEstoque());
        try (Connection c = banco.abrirConexao();
                PreparedStatement s = c.prepareStatement(
                        "INSERT INTO PRODUTOS VALUES (?, ?, ?, ?)")) {
            s.setString(1, p.getId());
            s.setString(2, p.getNome());
            s.setBigDecimal(3, BigDecimal.valueOf(p.getPreco()));
            s.setInt(4, p.getEstoque());
            s.executeUpdate();
        } catch (SQLException e) {
            throw falha(e);
        }
    }

    private Produto ler(ResultSet r) throws SQLException {
        return new Produto(r.getString("ID"), r.getString("NOME"),
                r.getBigDecimal("PRECO").doubleValue(), r.getInt("ESTOQUE"));
    }

    public Produto buscarPorId(String id) {
        try (Connection c = banco.abrirConexao();
                PreparedStatement s = c.prepareStatement(
                        "SELECT * FROM PRODUTOS WHERE ID = ?")) {
            s.setString(1, id);
            try (ResultSet r = s.executeQuery()) {
                return r.next() ? ler(r) : null;
            }
        } catch (SQLException e) {
            throw falha(e);
        }
    }

    public List<Produto> buscarTodos() {
        List<Produto> lista = new ArrayList<>();
        try (Connection c = banco.abrirConexao();
                PreparedStatement s = c.prepareStatement(
                        "SELECT * FROM PRODUTOS ORDER BY ID");
                ResultSet r = s.executeQuery()) {
            while (r.next())
                lista.add(ler(r));
            return lista;
        } catch (SQLException e) {
            throw falha(e);
        }
    }

    public void atualizar(String id, String nome, double preco, int estoque) {
        validar(id, nome, preco, estoque);
        try (Connection c = banco.abrirConexao();
                PreparedStatement s = c.prepareStatement("""
                        UPDATE PRODUTOS SET NOME=?, PRECO=?, ESTOQUE=? WHERE ID=?
                        """)) {
            s.setString(1, nome);
            s.setBigDecimal(2, BigDecimal.valueOf((int)preco));
            s.setInt(3, estoque);
            s.setString(4, id);
            if (s.executeUpdate() != 1)
                throw new IllegalArgumentException("Produto nao encontrado.");
        } catch (SQLException e) {
            throw falha(e);
        }
    }

    public void baixarEstoquesAtomico(Map<String, Integer> quantidades) {
        try (Connection c = banco.abrirConexao()) {
            c.setAutoCommit(false);
            try (PreparedStatement s = c.prepareStatement("""
                    UPDATE PRODUTOS SET ESTOQUE=ESTOQUE-?
                    WHERE ID=? AND ESTOQUE>=?
                    """)) {
                // Ordem fixa reduz conflito entre transações.
                for (var item : new TreeMap<>(quantidades).entrySet()) {
                    int q = item.getValue();
                    if (q < 0)
                        System.out.println(
                                "Quantidade deve ser positiva.");
                    s.setInt(1, q);
                    s.setString(2, item.getKey());
                    s.setInt(3, q);
                    if (s.executeUpdate() != 1)
                        throw new IllegalStateException(
                                "Produto ausente ou sem estoque: " + item.getKey());
                }
                c.commit();
            } catch (SQLException | RuntimeException e) {
                try {
                    c.rollback();
                } catch (SQLException rollback) {
                    e.addSuppressed(rollback);
                }
                throw e;
            }
        } catch (SQLException e) {
            throw falha(e);
        }
    }

    private void validar(String id, String nome, double preco, int estoque) {
        if (id == null || id.isBlank() || id.length() > 20
                || nome == null || nome.isBlank() || nome.length() > 100
                || !Double.isFinite(preco) || preco < 0
                || preco > 99999999.99 || estoque < 0)
            throw new IllegalArgumentException("Dados de produto invalidos.");
    }
}