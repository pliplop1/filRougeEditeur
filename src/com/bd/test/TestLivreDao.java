// src/com/bd/test/TestLivreDao.java
package com.bd.test;

import com.bd.dao.ConnecteurMysql;
import com.bd.dao.LivreDao;
import com.bd.entity.Livre;
import com.bd.exceptions.DaoException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public class TestLivreDao {

    public static void main(String[] args) {
        ConnecteurMysql connecteur = null;
        Connection connection = null;
        LivreDao livreDao = null;

        int testLivreId = 88888;
        String testLivreTitre = "Titre du Livre de Test DAO";

        try {
            connecteur = new ConnecteurMysql();
            connection = connecteur.getConnection();
            livreDao = new LivreDao(connection);

            System.out.println("--- Démarrage des tests LivreDao ---");

            System.out.println("\n--- Nettoyage initial ---");
            Livre existingLivre = livreDao.getLivreByTitre(testLivreTitre);
            if (existingLivre != null) {
                livreDao.deleteLivre(existingLivre.getId_livre());
                System.out.println("Livre de test existant avec titre '" + testLivreTitre + "' supprimé.");
            } else {
                System.out.println("Aucun livre de test existant à supprimer.");
            }

            System.out.println("\n--- Test d'ajout d'un livre ---");
            Livre newLivre = new Livre(testLivreTitre, "Auteur Test", "Description du livre de test.", BigDecimal.valueOf(25.50), "url_couverture.jpg", "uri_fichier.pdf", LocalDate.of(2022, 10, 26), 1);
            Livre addedLivre = livreDao.addLivre(newLivre);

            if (addedLivre != null && addedLivre.getId_livre() != 0) {
                System.out.println("Livre ajouté avec succès : " + addedLivre);
                testLivreId = addedLivre.getId_livre();
            } else {
                System.out.println("Échec de l'ajout du livre.");
                return;
            }

            System.out.println("\n--- Test de récupération par ID ---");
            Livre foundById = livreDao.getLivreById(testLivreId);
            if (foundById != null) {
                System.out.println("Livre trouvé par ID : " + foundById);
            } else {
                System.out.println("Livre non trouvé par ID " + testLivreId);
            }

            System.out.println("\n--- Test de récupération par titre ---");
            Livre foundByTitre = livreDao.getLivreByTitre(testLivreTitre);
            if (foundByTitre != null) {
                System.out.println("Livre trouvé par titre : " + foundByTitre);
            } else {
                System.out.println("Livre non trouvé par titre '" + testLivreTitre + "'");
            }


            System.out.println("\n--- Test de récupération de tous les livres ---");
            List<Livre> allLivres = livreDao.getAllLivres();
            System.out.println("Nombre total de livres : " + allLivres.size());
            for (Livre livre : allLivres) {
                System.out.println(livre);
            }

            System.out.println("\n--- Test de mise à jour d'un livre ---");
            if (foundById != null) {
                foundById.setPrix(BigDecimal.valueOf(29.99));
                foundById.setDescriptif("Description mise à jour pour le livre.");
                boolean updated = livreDao.updateLivre(foundById);
                System.out.println("Livre mis à jour : " + (updated ? "SUCCÈS" : "ÉCHEC"));
                if (updated) {
                    Livre checkUpdated = livreDao.getLivreById(testLivreId);
                    System.out.println("Vérification : " + checkUpdated);
                }
            }

            System.out.println("\n--- Test de suppression d'un livre ---");
            if (testLivreId != 0) {
                boolean deleted = livreDao.deleteLivre(testLivreId);
                System.out.println("Livre supprimé : " + (deleted ? "SUCCÈS" : "ÉCHEC"));
                if (deleted) {
                    System.out.println("Vérification : Livre avec ID " + testLivreId + " existe toujours ? " + (livreDao.getLivreById(testLivreId) != null ? "Oui" : "Non (Correct)"));
                }
            }

            System.out.println("\n--- Fin des tests LivreDao ---");

        } catch (DaoException e) {
            System.err.println("!!!! Une erreur DAO s'est produite lors des tests Livre !!!!");
            System.err.println("Message de l'erreur : " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("\n--- Nettoyage final des données de test ---");
            try {
                if (livreDao != null && livreDao.livreExists(testLivreId)) {
                    livreDao.deleteLivre(testLivreId);
                    System.out.println("Livre de test avec ID " + testLivreId + " supprimé (nettoyage final).");
                }
            } catch (DaoException e) {
                System.err.println("Erreur lors du nettoyage final du livre de test : " + e.getMessage());
                e.printStackTrace();
            }

            if (connecteur != null) {
                try {
                    connecteur.closeConnection();
                } catch (DaoException e) {
                    System.err.println("Erreur lors de la fermeture de la connexion du connecteur dans TestLivreDao : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}