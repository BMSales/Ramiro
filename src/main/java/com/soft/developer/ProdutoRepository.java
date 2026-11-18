package com.soft.developer;

import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

public class ProdutoRepository {

    public void salvar(Produto produto, Banco banco) throws SQLException {
        String produtoString = String.format("('%s', '%s', %.2f, %d)", produto.getId(), produto.getNome(), produto.getPreco(), produto.getEstoque());
        String sqlCadastro = "INSERT INTO PRODUTOS (ID, NOME, PRECO, ESTOQUE) VALUES " + produtoString;
        banco.comando.executeUpdate(sqlCadastro);
    }

    public ResultSet buscarPorId(String id, Banco banco) throws SQLException {
        String sqlConsulta = String.format("SELECT * FROM PRODUTOS WHERE ID = '%s'", id);
        return banco.comando.executeQuery(sqlConsulta);
    }

    public ResultSet buscarTodos(Banco banco) throws SQLException{
        String sqlConsulta = "SELECT * FROM PRODUTOS";
        return banco.comando.executeQuery(sqlConsulta);
    }
}
