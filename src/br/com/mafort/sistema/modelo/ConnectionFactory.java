package br.com.mafort.sistema.modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    public Connection getConnection() {
        String currentDirectory = System.getProperty("user.dir");
        String dbFilePath = currentDirectory + "/exemploempresa.sqlite";
        try {
            return DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter a conexão com o banco de dados: " + e.getMessage(), e);
        }
    }
}
