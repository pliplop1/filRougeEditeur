// src/com/bd/dao/ConnecteurMysql.java
package com.bd.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.bd.exceptions.DaoException;

public class ConnecteurMysql {

    // Informations de connexion à votre base de données
    private static final String DB_URL = "jdbc:mysql://localhost:3306/filrouge"; // REMPLACEZ "votre_bd" par le nom de votre base de données
    private static final String DB_USER = "root"; // REMPLACEZ "votre_utilisateur" par votre nom d'utilisateur MySQL
    private static final String DB_PASSWORD = "root"; // REMPLACEZ "votre_mot_de_passe" par votre mot de passe MySQL

    private Connection connection; // L'objet Connection sera géré en interne

    public ConnecteurMysql() throws DaoException {
        try {
            // Charger le pilote JDBC (si nécessaire, pas toujours pour JDBC 4.0+)
            // Class.forName("com.mysql.cj.jdbc.Driver"); // Décommenter si vous avez des problèmes de pilote non trouvé
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connexion à la base de données établie avec succès.");
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de l'établissement de la connexion à la base de données: " + e.getMessage(), e);
        }
    }

    // CORRECTION : Méthode pour obtenir la connexion
    public Connection getConnection() {
        return this.connection;
    }

    // CORRECTION : Méthode pour fermer la connexion (sans argument)
    public void closeConnection() throws DaoException {
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