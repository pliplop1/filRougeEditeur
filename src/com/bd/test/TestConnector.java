package com.bd.test;

import java.sql.Connection;

import com.bd.dao.ConnecteurMysql;
import com.bd.exceptions.DaoException; // Nécessaire pour capturer DaoException

public class TestConnector {

    public static void main(String[] args) {
        ConnecteurMysql connecteur = null; // Déclarez en dehors du try pour l'utiliser dans finally
        Connection connection = null; // Déclarez en dehors du try

        try {
            connecteur = new ConnecteurMysql(); // Cette ligne peut maintenant lancer DaoException
            connection = connecteur.getConnection(); // CORRECTION : Utilisez getConnection() au lieu de getCn()

            if (connection != null) {
                System.out.println("Connexion au connecteur réussie: " + connection);
            } else {
                // Ce bloc else est techniquement moins probable maintenant,
                // car si la connexion échoue, une DaoException est normalement lancée.
                System.err.println("Échec inattendu de la connexion au connecteur (connexion est null mais pas d'exception lancée).");
            }

        } catch (DaoException e) {
            // Capture la DaoException lancée par le constructeur de ConnecteurMysql
            System.err.println("!!!! ERREUR : Échec de l'initialisation du connecteur ou de la connexion à la base de données. !!!!");
            System.err.println("Message : " + e.getMessage());
            e.printStackTrace(); // Affiche la trace complète pour le débogage (inclut l'SQLException originale)
        } finally {
            // Assurez-vous que la connexion est fermée, même si une exception s'est produite
            if (connecteur != null) { // Vérifie que l'objet connecteur a été instancié
                try {
                    connecteur.closeConnection(); // Appelle la nouvelle méthode pour fermer la connexion
                } catch (DaoException e) {
                    System.err.println("Erreur lors de la fermeture de la connexion du connecteur : " + e.getMessage());
                    e.printStackTrace(); // Log l'erreur de fermeture, sans masquer l'exception originale du try
                }
            }
        }
    }
}