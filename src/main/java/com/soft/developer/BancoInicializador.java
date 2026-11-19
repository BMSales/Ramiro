package com.soft.developer;

import java.sql.*;

public class BancoInicializador {
    public static void preparar(Banco banco) throws SQLException {
        try (Connection c = banco.abrirConexao();
                Statement s = c.createStatement()) {
            s.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS PRODUTOS (
                    ID VARCHAR(20) PRIMARY KEY,
                    NOME VARCHAR(100) NOT NULL,
                    PRECO DECIMAL(10,2) NOT NULL CHECK (PRECO >= 0),
                    ESTOQUE INT NOT NULL CHECK (ESTOQUE >= 0))
                    """);
            // Marcador independente do conteúdo do catálogo.
            s.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS CONFIGURACAO (
                    CHAVE VARCHAR(40) PRIMARY KEY)
                    """);
            c.setAutoCommit(false);
            try {
                boolean pronta;
                try (ResultSet r = s.executeQuery("""
                        SELECT CHAVE FROM CONFIGURACAO
                        WHERE CHAVE = 'CARGA_INICIAL'
                        """)) {
                    pronta = r.next();
                }
                if (!pronta) {
                    s.executeUpdate("""
                            INSERT INTO PRODUTOS VALUES
                            ('1','Arroz 5kg',24.90,15),
                            ('2','Feijao Preto 1kg',8.50,20),
                            ('3','Oleo de Soja',6.80,30),
                            ('4','Macarrao Spagetti',4.20,25),
                            ('5','Cafe Torrado 500g',14.90,12)
                            """);
                    s.executeUpdate("""
                            INSERT INTO CONFIGURACAO VALUES ('CARGA_INICIAL')
                            """);
                }
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        }
    }
}