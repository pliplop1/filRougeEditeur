// src/com/bd/test/TestCategorieDao.java
package com.bd.test;

import com.bd.dao.CategorieDao;
import com.bd.dao.ConnecteurMysql;
import com.bd.entity.Categorie;
import com.bd.exceptions.DaoException;
import java.sql.Connection;
import java.util.List;

public class TestCategorieDao {

    public static void main(String[] args) {
        ConnecteurMysql connecteur = null;
        Connection connection = null;
        CategorieDao categorieDao = null;

        int testCategorieId = 66666;
        String testCategorieNom = "Categorie Test DAO";

        try {
            connecteur = new ConnecteurMysql();
            connection = connecteur.getConnection();
            categorieDao = new CategorieDao(connection);

            System.out.println("--- Démarrage des tests CategorieDao ---");

            System.out.println("\n--- Nettoyage initial ---");
            Categorie existingCategorie = categorieDao.getCategorieByNom(testCategorieNom);
            if (existingCategorie != null) {
                categorieDao.deleteCategorie(existingCategorie.getId_categorie());
                System.out.println("Catégorie de test existante avec nom '" + testCategorieNom + "' supprimée.");
            } else {
                System.out.println("Aucune catégorie de test existante à supprimer.");
            }

            System.out.println("\n--- Test d'ajout d'une catégorie ---");
            Categorie newCategorie = new Categorie(testCategorieNom);
            Categorie addedCategorie = categorieDao.addCategorie(newCategorie);

            if (addedCategorie != null && addedCategorie.getId_categorie() != 0) {
                System.out.println("Catégorie ajoutée avec succès : " + addedCategorie);
                testCategorieId = addedCategorie.getId_categorie();
            } else {
                System.out.println("Échec de l'ajout de la catégorie.");
                return;
            }

            System.out.println("\n--- Test de récupération par ID ---");
            Categorie foundById = categorieDao.getCategorieById(testCategorieId);
            if (foundById != null) {
                System.out.println("Catégorie trouvée par ID : " + foundById);
            } else {
                System.out.println("Catégorie non trouvée par ID " + testCategorieId);
            }

            System.out.println("\n--- Test de récupération par nom ---");
            Categorie foundByNom = categorieDao.getCategorieByNom(testCategorieNom);
            if (foundByNom != null) {
                System.out.println("Catégorie trouvée par nom : " + foundByNom);
            } else {
                System.out.println("Catégorie non trouvée par nom '" + testCategorieNom + "'");
            }

            System.out.println("\n--- Test de récupération de toutes les catégories ---");
            List<Categorie> allCategories = categorieDao.getAllCategories();
            System.out.println("Nombre total de catégories : " + allCategories.size());
            for (Categorie cat : allCategories) {
                System.out.println(cat);
            }

            System.out.println("\n--- Test de mise à jour d'une catégorie ---");
            if (foundById != null) {
                foundById.setNom_categorie("Nom Catégorie Mis à Jour");
                boolean updated = categorieDao.updateCategorie(foundById);
                System.out.println("Catégorie mise à jour : " + (updated ? "SUCCÈS" : "ÉCHEC"));
                if (updated) {
                    Categorie checkUpdated = categorieDao.getCategorieById(testCategorieId);
                    System.out.println("Vérification : " + checkUpdated);
                }
            }

            System.out.println("\n--- Test de suppression d'une catégorie ---");
            if (testCategorieId != 0) {
                boolean deleted = categorieDao.deleteCategorie(testCategorieId);
                System.out.println("Catégorie supprimée : " + (deleted ? "SUCCÈS" : "ÉCHEC"));
                if (deleted) {
                    System.out.println("Vérification : Catégorie avec ID " + testCategorieId + " existe toujours ? " + (categorieDao.getCategorieById(testCategorieId) != null ? "Oui" : "Non (Correct)"));
                }
            }

            System.out.println("\n--- Fin des tests CategorieDao ---");

        } catch (DaoException e) {
            System.err.println("!!!! Une erreur DAO s'est produite lors des tests Catégorie !!!!");
            System.err.println("Message de l'erreur : " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("\n--- Nettoyage final des données de test ---");
            try {
                if (categorieDao != null && categorieDao.categorieExists(testCategorieId)) {
                    categorieDao.deleteCategorie(testCategorieId);
                    System.out.println("Catégorie de test avec ID " + testCategorieId + " supprimée (nettoyage final).");
                }
            } catch (DaoException e) {
                System.err.println("Erreur lors du nettoyage final de la catégorie de test : " + e.getMessage());
                e.printStackTrace();
            }

            if (connecteur != null) {
                try {
                    connecteur.closeConnection();
                } catch (DaoException e) {
                    System.err.println("Erreur lors de la fermeture de la connexion du connecteur dans TestCategorieDao : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}