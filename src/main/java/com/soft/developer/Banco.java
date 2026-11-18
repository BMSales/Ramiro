package com.soft.developer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Banco {
    private String url;
    private String usuario;
    private String senha;

    public Statement comando;

    public void abrirConexao() throws SQLException{
        this.url = "jdbc:h2:mem:demo_aula1;DB_CLOSE_DELAY=-1";
        this.usuario = "sa";
        this.senha = "";
        try (Connection conexao = DriverManager.getConnection(url, usuario, senha); Statement comando = conexao.createStatement()){
            this.comando = comando;
        }
    }
}