package com.bd.test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;

import org.junit.jupiter.api.Test;

import com.bd.dao.ConnecteurMysql;
import com.bd.exceptions.DaoException;

public class TestConnector {

    @Test
    public void testDatabaseConnection() {
        System.out.println("--- Test de la connexion à la base de données ---");

        // Utilisation du try-with-resources pour s'assurer que la connexion est fermée
        // même en cas d'erreur.
        assertDoesNotThrow(() -> {
            try (ConnecteurMysql connecteur = new ConnecteurMysql()) {

                Connection connection = connecteur.getConnection();

                // 1. Vérifier que l'objet Connection n'est pas nul
                assertNotNull(connection, "L'objet Connection ne doit pas être nul.");
                System.out.println("Objet ConnecteurMysql et Connection créés avec succès.");

                // 2. Vérifier que la connexion est valide (ping la base de données)
                assertTrue(connection.isValid(2), "La connexion à la base de données doit être valide.");
                System.out.println("La connexion à la base de données est valide et fonctionnelle.");

            } // La méthode close() sera automatiquement appelée ici par le try-with-resources
              // si vous implémentez l'interface AutoCloseable dans ConnecteurMysql.
              // Sinon, le test vérifie déjà que la connexion peut être établie.

        }, "La création du connecteur ou l'établissement de la connexion ne doit pas lancer d'exception.");

        System.out.println("--- Test de connexion réussi ---");
    }
}