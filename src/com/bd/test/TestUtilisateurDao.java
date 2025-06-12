// src/com/bd/test/TestUtilisateurDao.java
package com.bd.test;

import com.bd.dao.ConnecteurMysql;
import com.bd.dao.UtilisateurDao;
import com.bd.entity.Utilisateur;
import com.bd.exceptions.DaoException;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public class TestUtilisateurDao {

    public static void main(String[] args) {
        ConnecteurMysql connecteur = null;
        Connection connection = null;
        UtilisateurDao utilisateurDao = null;

        int testUserId = 99999;
        String testUserEmail = "test.utilisateur.dao@example.com";

        try {
            connecteur = new ConnecteurMysql();
            connection = connecteur.getConnection();
            utilisateurDao = new UtilisateurDao(connection);

            System.out.println("--- Démarrage des tests UtilisateurDao ---");

            System.out.println("\n--- Nettoyage initial ---");
            Utilisateur existingUser = utilisateurDao.getUtilisateurByEmail(testUserEmail);
            if (existingUser != null) {
                utilisateurDao.deleteUtilisateur(existingUser.getId_utilisateur());
                System.out.println("Utilisateur de test existant avec email " + testUserEmail + " supprimé.");
            } else {
                System.out.println("Aucun utilisateur de test existant à supprimer.");
            }

            System.out.println("\n--- Test d'ajout d'un utilisateur ---");
            Utilisateur newUser = new Utilisateur("NomTest", "PrenomTest", testUserEmail, "motdepassehache", "123 Rue de Java", "Codetown", "75000", "France", LocalDate.of(2023, 1, 15), "client");
            Utilisateur addedUser = utilisateurDao.addUtilisateur(newUser);

            if (addedUser != null && addedUser.getId_utilisateur() != 0) {
                System.out.println("Utilisateur ajouté avec succès : " + addedUser);
                testUserId = addedUser.getId_utilisateur();
            } else {
                System.out.println("Échec de l'ajout de l'utilisateur.");
                return;
            }

            System.out.println("\n--- Test de récupération par ID ---");
            Utilisateur foundById = utilisateurDao.getUtilisateurById(testUserId);
            if (foundById != null) {
                System.out.println("Utilisateur trouvé par ID : " + foundById);
            } else {
                System.out.println("Utilisateur non trouvé par ID " + testUserId);
            }

            System.out.println("\n--- Test de récupération par email ---");
            Utilisateur foundByEmail = utilisateurDao.getUtilisateurByEmail(testUserEmail);
            if (foundByEmail != null) {
                System.out.println("Utilisateur trouvé par email : " + foundByEmail);
            } else {
                System.out.println("Utilisateur non trouvé par email " + testUserEmail);
            }

            System.out.println("\n--- Test de récupération de tous les utilisateurs ---");
            List<Utilisateur> allUsers = utilisateurDao.getAllUtilisateurs();
            System.out.println("Nombre total d'utilisateurs : " + allUsers.size());
            for (Utilisateur user : allUsers) {
                System.out.println(user);
            }

            System.out.println("\n--- Test de mise à jour d'un utilisateur ---");
            if (foundById != null) {
                foundById.setVille("NouvelleVille");
                foundById.setRole("admin");
                boolean updated = utilisateurDao.updateUtilisateur(foundById);
                System.out.println("Utilisateur mis à jour : " + (updated ? "SUCCÈS" : "ÉCHEC"));
                if (updated) {
                    Utilisateur checkUpdated = utilisateurDao.getUtilisateurById(testUserId);
                    System.out.println("Vérification : " + checkUpdated);
                }
            }

            System.out.println("\n--- Test de suppression d'un utilisateur ---");
            if (testUserId != 0) {
                boolean deleted = utilisateurDao.deleteUtilisateur(testUserId);
                System.out.println("Utilisateur supprimé : " + (deleted ? "SUCCÈS" : "ÉCHEC"));
                if (deleted) {
                    System.out.println("Vérification : Utilisateur avec ID " + testUserId + " existe toujours ? " + (utilisateurDao.getUtilisateurById(testUserId) != null ? "Oui" : "Non (Correct)"));
                }
            }

            System.out.println("\n--- Fin des tests UtilisateurDao ---");

        } catch (DaoException e) {
            System.err.println("!!!! Une erreur DAO s'est produite lors des tests Utilisateur !!!!");
            System.err.println("Message de l'erreur : " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("\n--- Nettoyage final des données de test ---");
            try {
                if (utilisateurDao != null && utilisateurDao.utilisateurExists(testUserId)) {
                    utilisateurDao.deleteUtilisateur(testUserId);
                    System.out.println("Utilisateur de test avec ID " + testUserId + " supprimé (nettoyage final).");
                }
            } catch (DaoException e) {
                System.err.println("Erreur lors du nettoyage final de l'utilisateur de test : " + e.getMessage());
                e.printStackTrace();
            }

            if (connecteur != null) {
                try {
                    connecteur.closeConnection();
                } catch (DaoException e) {
                    System.err.println("Erreur lors de la fermeture de la connexion du connecteur dans TestUtilisateurDao : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}