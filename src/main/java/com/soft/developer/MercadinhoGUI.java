package com.soft.developer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class MercadinhoGUI extends JFrame {
    private final ProdutoRepository repo;
    private final VendaService service;
    private final CarrinhoCompras carrinho;
    private final DefaultTableModel produtos = modelo(
            "ID", "Nome", "Preco", "Estoque");
    private final DefaultTableModel itens = modelo(
            "Produto", "Qtd", "Subtotal");
    private final JTable tabela = new JTable(produtos);
    private final JLabel total = new JLabel("Total: R$ 0,00");
    private final JSpinner quantidade = new JSpinner(
            new SpinnerNumberModel(1, 1, 100, 1));

    private static DefaultTableModel modelo(String... colunas) {
        return new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int l, int c) {
                return false;
            }
        };
    }

    public MercadinhoGUI(ProdutoRepository repo) {
        super("Mercadinho - H2 persistente");
        this.repo = repo;
        service = new VendaService(repo);
        carrinho = service.criarCarrinho();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        JPanel paineis = new JPanel(new GridLayout(1, 2, 10, 10));
        paineis.add(new JScrollPane(tabela));
        paineis.add(new JScrollPane(new JTable(itens)));
        add(paineis, BorderLayout.CENTER);
        JPanel botoes = new JPanel();
        JButton adicionar = new JButton("Adicionar");
        JButton limpar = new JButton("Limpar carrinho");
        JButton finalizar = new JButton("Finalizar compra");
        JButton editar = new JButton("Editar produto");
        JButton novo = new JButton("Novo produto");
        botoes.add(quantidade);
        botoes.add(adicionar);
        botoes.add(limpar);
        botoes.add(finalizar);
        botoes.add(editar);
        botoes.add(novo);
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.add(botoes, BorderLayout.CENTER);
        rodape.add(total, BorderLayout.SOUTH);
        add(rodape, BorderLayout.SOUTH);
        adicionar.addActionListener(e -> executar(() -> {
            service.adicionarProduto(carrinho, idSelecionado(),
                    (Integer) quantidade.getValue());
            atualizarCarrinho();
        }));
        limpar.addActionListener(e -> {
            carrinho.limpar();
            atualizarCarrinho();
        });
        finalizar.addActionListener(e -> executar(() -> {
            service.finalizarVenda(carrinho);
            atualizarCarrinho();
            recarregar();
            JOptionPane.showMessageDialog(this, "Compra gravada no banco.");
        }));
        editar.addActionListener(e -> executar(() -> editar(false)));
        novo.addActionListener(e -> executar(() -> editar(true)));
        recarregar();
    }

    private String idSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0)
            throw new IllegalArgumentException("Selecione um produto.");
        return produtos.getValueAt(linha, 0).toString();
    }

    private void executar(Runnable acao) {
        try {
            acao.run();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Operacao nao concluida", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void recarregar() {
        produtos.setRowCount(0);
        for (Produto p : repo.buscarTodos())
            produtos.addRow(new Object[] {
                    p.getId(), p.getNome(), String.format("%.2f", p.getPreco()),
                    p.getEstoque() });
    }

    private void atualizarCarrinho() {
        itens.setRowCount(0);
        for (ItemCarrinho i : carrinho.getItens())
            itens.addRow(new Object[] {
                    i.getProduto().getNome(), i.getQuantidade(),
                    String.format("%.2f", i.getSubtotal()) });
        total.setText(String.format("Total: R$ %.2f", carrinho.calcularTotal()));
    }

    private void editar(boolean novo) {
        if (!carrinho.getItens().isEmpty())
            throw new IllegalStateException(
                    "Finalize ou limpe o carrinho antes de editar o catalogo.");
        Produto p = novo ? null : repo.buscarPorId(idSelecionado());
        JTextField id = new JTextField(novo ? "" : p.getId());
        id.setEditable(novo);
        JTextField nome = new JTextField(novo ? "" : p.getNome());
        JTextField preco = new JTextField(novo ? "" : "" + p.getPreco());
        JTextField estoque = new JTextField(novo ? "0" : "" + p.getEstoque());
        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("ID"));
        form.add(id);
        form.add(new JLabel("Nome"));
        form.add(nome);
        form.add(new JLabel("Preco (ex.: 24,90)"));
        form.add(preco);
        form.add(new JLabel("Estoque"));
        form.add(estoque);
        if (JOptionPane.showConfirmDialog(this, form, "Produto",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
            return;
        double valor = Double.parseDouble(preco.getText().trim().replace(',', '.'));
        int qtd = Integer.parseInt(estoque.getText().trim());
        if (novo)
            repo.salvar(new Produto(id.getText().trim(),
                    nome.getText().trim(), valor, qtd));
        else
            repo.atualizar(p.getId(), nome.getText().trim(), valor, qtd);
        recarregar();
    }

    public static void main(String[] args) {
        Banco banco = new Banco();
        try {
            BancoInicializador.preparar(banco);
            System.out.println("Banco: " + banco.getUrl());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao abrir banco: " + e);
            return;
        }
        SwingUtilities.invokeLater(() -> {
            try {
                new MercadinhoGUI(new ProdutoRepository(banco)).setVisible(true);
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        });
    }
}