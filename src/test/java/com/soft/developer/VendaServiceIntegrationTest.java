package com.soft.developer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VendaServiceIntegrationTest {

    @Test
    void deveRealizarVendaCompleta() {
        Banco banco = new Banco();

        banco.abrirConexao();
        ProdutoRepository repository = new ProdutoRepository();
        repository.salvar(new Produto("1", "Arroz", 25.00, 10), banco);

        VendaService service = new VendaService(repository);
        CarrinhoCompras carrinho = service.criarCarrinho();

        service.adicionarProduto(carrinho,"1",2);

        assertEquals(50.00, carrinho.calcularTotal(), 0.001);
        service.finalizarVenda(carrinho, banco);

        Produto produto = repository.buscarPorId("1");
        assertEquals(8, produto.getEstoque());
        assertTrue(carrinho.getItens().isEmpty());
    }
}