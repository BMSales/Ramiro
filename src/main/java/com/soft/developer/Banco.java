package com.soft.developer;

import java.sql.*;

public class Banco {
    private final String url;

    public Banco() {
        this("jdbc:h2:file:~/mercadinho-dados/mercadinho");
    }

    public Banco(String url) {
        this.url = url;
    }

    public Connection abrirConexao() throws SQLException {
        return DriverManager.getConnection(url, "sa", "");
    }

    public String getUrl() {
        return url;
    }
}