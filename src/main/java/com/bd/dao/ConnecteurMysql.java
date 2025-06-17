// src/com/bd/dao/ConnecteurMysql.java
package com.bd.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.bd.exceptions.DaoException;

// 1. Implémenter l'interface
public class ConnecteurMysql implements AutoCloseable {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/filrouge";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    private Connection connection;

    public ConnecteurMysql() throws DaoException {
        try {
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connexion à la base de données établie avec succès.");
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de l'établissement de la connexion à la base de données: " + e.getMessage(), e);
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    // 2. Renommer la méthode en "close"
    @Override
    public void close() throws DaoException {
        if (this.connection != null) {
            try {
                this.connection.close();
                System.out.println("Connexion à la base de données fermée.");
            } catch (SQLException e) {
                throw new DaoException("Erreur lors de la fermeture de la connexion à la base de données: " + e.getMessage(), e);
            }
        }
    }
}