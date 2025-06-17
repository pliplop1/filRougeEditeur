package com.bd.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.bd.dao.CategorieDao;
import com.bd.dao.ConnecteurMysql;
import com.bd.entity.Categorie;
import com.bd.exceptions.DaoException;

// Ordonne l'exécution des tests
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestCategorieDao {

    // On déclare les variables en static pour qu'elles soient partagées entre les tests
    private static ConnecteurMysql connecteur;
    private static Connection connection;
    private static CategorieDao categorieDao;
    private static int testCategorieId; // pour stocker l'ID de la catégorie créée
    private static final String testCategorieNom = "Categorie Test JUnit";

    // Cette méthode est exécutée UNE SEULE FOIS avant tous les tests
    @BeforeAll
    public static void setUp() throws DaoException {
        connecteur = new ConnecteurMysql();
        connection = connecteur.getConnection();
        categorieDao = new CategorieDao(connection);

        System.out.println("--- Démarrage des tests CategorieDao ---");

        // Nettoyage initial au cas où un test précédent aurait échoué
        Categorie existingCategorie = categorieDao.getCategorieByNom(testCategorieNom);
        if (existingCategorie != null) {
            categorieDao.deleteCategorie(existingCategorie.getId_categorie());
            System.out.println("Catégorie de test existante nettoyée.");
        }
    }

    // Chaque méthode @Test est un cas de test individuel
    @Test
    @Order(1) // Le premier test à exécuter
    public void testAddCategorie() throws DaoException {
        System.out.println("\n--- Test d'ajout ---");
        Categorie newCategorie = new Categorie(testCategorieNom);
        Categorie addedCategorie = categorieDao.addCategorie(newCategorie);

        assertNotNull(addedCategorie, "La catégorie ajoutée ne doit pas être nulle.");
        assertTrue(addedCategorie.getId_categorie() > 0, "L'ID de la catégorie doit être positif.");
        
        testCategorieId = addedCategorie.getId_categorie(); // On sauvegarde l'ID pour les tests suivants
        System.out.println("Catégorie ajoutée avec succès : " + addedCategorie);
    }

    @Test
    @Order(2) // Le deuxième test
    public void testGetCategorieById() throws DaoException {
        System.out.println("\n--- Test de récupération par ID ---");
        Categorie foundById = categorieDao.getCategorieById(testCategorieId);
        
        assertNotNull(foundById, "La catégorie doit être trouvée par son ID.");
        assertEquals(testCategorieId, foundById.getId_categorie(), "Les IDs doivent correspondre.");
        System.out.println("Catégorie trouvée par ID : " + foundById);
    }
    
    @Test
    @Order(3) // Le troisième test
    public void testUpdateCategorie() throws DaoException {
        System.out.println("\n--- Test de mise à jour ---");
        Categorie categorieToUpdate = categorieDao.getCategorieById(testCategorieId);
        categorieToUpdate.setNom_categorie("Nom Catégorie Mis à Jour");
        
        boolean updated = categorieDao.updateCategorie(categorieToUpdate);
        
        assertTrue(updated, "La mise à jour doit retourner true.");
        
        Categorie checkUpdated = categorieDao.getCategorieById(testCategorieId);
        assertEquals("Nom Catégorie Mis à Jour", checkUpdated.getNom_categorie(), "Le nom de la catégorie doit avoir été mis à jour.");
        System.out.println("Vérification de la mise à jour : " + checkUpdated);
    }

    @Test
    @Order(4) // Le quatrième test
    public void testDeleteCategorie() throws DaoException {
        System.out.println("\n--- Test de suppression ---");
        boolean deleted = categorieDao.deleteCategorie(testCategorieId);
        
        assertTrue(deleted, "La suppression doit retourner true.");
        
        Categorie shouldBeNull = categorieDao.getCategorieById(testCategorieId);
        assertNull(shouldBeNull, "La catégorie ne doit plus exister après suppression.");
        System.out.println("Catégorie supprimée avec succès.");
    }

    // Cette méthode est exécutée UNE SEULE FOIS après tous les tests
    @AfterAll
    public static void tearDown() throws DaoException {
        if (connecteur != null) {
            connecteur.close();
            System.out.println("\n--- Connexion à la base de données fermée ---");
        }
    }
}