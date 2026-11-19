package com.soft.developer;

import java.util.*;

public class VendaService {
    private final ProdutoRepository repository;

    public VendaService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public CarrinhoCompras criarCarrinho() {
        return new CarrinhoCompras();
    }

    public void adicionarProduto(CarrinhoCompras carrinho, String id, int q) {
        Produto p = repository.buscarPorId(id);
        if (p == null)
            throw new IllegalArgumentException("Produto nao encontrado.");
        carrinho.adicionarProduto(p, q);
    }

    public void finalizarVenda(CarrinhoCompras carrinho) {
        if (carrinho.getItens().isEmpty())
            throw new IllegalStateException("O carrinho esta vazio.");
        Map<String, Integer> quantidades = new TreeMap<>();
        for (ItemCarrinho item : carrinho.getItens()) {
            quantidades.merge(item.getProduto().getId(),
                    item.getQuantidade(), Math::addExact);
        }
        repository.baixarEstoquesAtomico(quantidades);
        carrinho.limpar();
    }
}