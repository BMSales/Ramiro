package com.soft.developer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Banco {
    private String url;
    private String usuario;
    private String senha;

    public Statement abrirConexao() throws SQLException{
        url = "jdbc:h2:mem:demo_aula1;DB_CLOSE_DELAY=-1";
        usuario = "sa";
        senha = "";
        try (Connection conexao = DriverManager.getConnection(url, usuario, senha); Statement comando = conexao.createStatement()){
            return comando;
        }
    }
}