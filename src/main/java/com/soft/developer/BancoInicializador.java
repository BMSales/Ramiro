package com.soft.developer;

import java.sql.SQLException;

public class BancoInicializador {

    public void criarEsquema(Banco banco) {
        String sqlCriacao = "CREATE TABLE PRODUTOS (ID VARCHAR(20) PRIMARY KEY, NOME VARCHAR(100) NOT NULL, PRECO DECIMAL(10,2) NOT NULL CHECK (PRECO >= 0), ESTOQUE INT NOT NULL CHECK (ESTOQUE >= 0))";
        try{
            banco.comando.executeUpdate(sqlCriacao);
        }
        catch(SQLException e){
            System.out.println(e);
        }

    }
    public void popularRepositorio(Banco banco, ProdutoRepository repository){
        repository.salvar(new Produto("1", "Arroz 5kg", 24.90, 15), banco);
        repository.salvar(new Produto("2", "Feijão Preto 1kg", 8.50, 20), banco);
        repository.salvar(new Produto("3", "Óleo de Soja", 6.80, 30), banco);
        repository.salvar(new Produto("4", "Macarrão Spagetti", 4.20, 25), banco);
        repository.salvar(new Produto("5", "Café Torrado 500g", 14.90, 12), banco);
    }
}