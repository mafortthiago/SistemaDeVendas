package br.com.mafort.sistema.modelo;


import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CidadeRepository {
    private Connection connection;

    public CidadeRepository() {
        connection = new ConnectionFactory().getConnection();
    }

    public Cidade retornaCidadePeloId(int id) {
        String instrucaoSQL = "SELECT nome, uf_id FROM cidade WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(instrucaoSQL)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Cidade(new String(rs.getBytes("nome"), StandardCharsets.UTF_8), rs.getInt("uf_id"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public List<String> obtemNomeCidade(String uf) {
        List<String> nomeCidades = new ArrayList<>();
        String instrucaoSQL = "SELECT c.nome FROM cidade c JOIN uf ON (c.uf_id = uf.id) WHERE uf.sigla = ?";

        try (PreparedStatement stmt = connection.prepareStatement(instrucaoSQL)) {
            stmt.setString(1, uf);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String cidade = new String(rs.getBytes("nome"), StandardCharsets.UTF_8); // Decodifica como UTF-8
                    nomeCidades.add(cidade);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nomeCidades;
    }

    public int obtemIdDaCidadeSelecionada(String cidade) {
        String instrucaoSQL = "SELECT id FROM cidade WHERE nome = ?";
        int idCidade = 0;

        try (PreparedStatement stmt = connection.prepareStatement(instrucaoSQL)) {
            stmt.setString(1, cidade);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    idCidade = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idCidade;
    }

}
