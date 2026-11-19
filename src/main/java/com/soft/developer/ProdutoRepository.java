package com.soft.developer;

import java.util.ArrayList;
import java.util.List;

import java.sql.SQLException;

public class ProdutoRepository {

    private final List<Produto> produtos = new ArrayList<>();

    public void atualizarEstoque(Produto produto, int quantidade, Banco banco) {
        if(quantidade <= 0){
            return;
        }
        String sqlUpdate = String.format("UPDATE PRODUTOS SET ESTOQUE = %d WHERE ID = '%s'", quantidade, produto.getId());
        try {
            banco.comando.executeUpdate(sqlUpdate);
        }
        catch(SQLException e){
            System.out.println(e);
        }
    }

    public void salvar(Produto produto, Banco banco) {
        String produtoString = String.format("('%s', '%s', %.2f, %d)", produto.getId(), produto.getNome(), produto.getPreco(), produto.getEstoque());
        String sqlCadastro = "INSERT INTO PRODUTOS (ID, NOME, PRECO, ESTOQUE) VALUES " + produtoString;
        try {
            banco.comando.executeUpdate(sqlCadastro);
        }
        catch(SQLException e){
            System.out.println(e);
        }
        produtos.add(produto);
    }

    public Produto buscarPorId(String id) {
        return produtos.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
    }

    public List<Produto> buscarTodos() {
        return produtos;
    }
}
