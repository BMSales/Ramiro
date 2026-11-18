package com.soft.developer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

public class BancoDemo {
    public static void main(String[] args) throws SQLException {

        String url = "jdbc:h2:mem:demo_aula1;DB_CLOSE_DELAY=-1";

        try (Connection conexao = DriverManager.getConnection(url, "sa", ""); Statement comando = conexao.createStatement()){
            System.out.println("1. Conexao com H2 aberta.");

            String sqlCriacao = "CREATE TABLE PRODUTOS (ID VARCHAR(20) PRIMARY KEY, NOME VARCHAR(100) NOT NULL, PRECO DECIMAL(10,2) NOT NULL CHECK (PRECO >= 0), ESTOQUE INT NOT NULL CHECK (ESTOQUE >= 0))";
            comando.executeUpdate(sqlCriacao);
            System.out.println("2. Tabela PRODUTOS criada.");

            String sqlCadastro = "INSERT INTO PRODUTOS (ID, NOME, PRECO, ESTOQUE) VALUES ('1', 'Arroz 5kg', 24.90, 18), ('2', 'Feijao Preto 1kg', 8.50, 20)";
            int cadastrados = comando.executeUpdate(sqlCadastro);
            System.out.println("3. Produtos cadastrados: " + cadastrados);

            System.out.println("4. Consulta do catalogo:");
            String sqlConsulta = "SELECT ID, NOME, PRECO, ESTOQUE " + "FROM PRODUTOS ORDER BY ID";

            try(ResultSet resultado = comando.executeQuery(sqlConsulta)){
                while(resultado.next()) {
                    System.out.printf(Locale.ROOT, "%s | %s |%.2f | %d%n",
                        resultado.getString("ID"),
                        resultado.getString("NOME"),
                        resultado.getBigDecimal("PRECO"),
                        resultado.getInt("ESTOQUE"));
                }
            }

            comando.executeUpdate("UPDATE PRODUTOS SET ESTOQUE = 13 WHERE ID = '1'");

            try(ResultSet resultado = comando.executeQuery("SELECT ESTOQUE FROM PRODUTOS WHERE ID = '1'")){
                if(resultado.next()){
                    System.out.println("5. Estoque do arroz apos UPDATE: " + resultado.getInt("ESTOQUE"));
                }
            }

            int removidos = comando.executeUpdate("DELETE FROM PRODUTOS WHERE ID = '2'");
            System.out.println("6. Produtos removidos: " + removidos);
            System.out.println("7. Demonstracao concluida");
        }
    }
}
        