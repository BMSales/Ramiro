package com.soft.developer;

import java.sql.SQLException;

public class BancoInicializador {

    public void criarEsquema(Banco banco) throws SQLException {
        String sqlCriacao = "CREATE TABLE PRODUTOS (ID VARCHAR(20) PRIMARY KEY, NOME VARCHAR(100) NOT NULL, PRECO DECIMAL(10,2) NOT NULL CHECK (PRECO >= 0), ESTOQUE INT NOT NULL CHECK (ESTOQUE >= 0))";
        banco.comando.executeUpdate(sqlCriacao);

        String sqlCadastro = "INSERT INTO PRODUTOS (ID, NOME, PRECO, ESTOQUE) VALUES ('1', 'Arroz 5kg', 24.90, 15), ('2', 'Feijao Preto 1kg', 8.50, 20), ('3', 'Oleo de Soja', 6.80, 30), ('4', 'Macarrao Spagetthi', 4.20, 25), ('5', 'Cafe Torrado 500g', 14.90, 12)";
        banco.comando.executeUpdate(sqlCadastro);
    }
}