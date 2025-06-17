package com.bd.test;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.bd.dao.CategorieDao;
import com.bd.dao.ConnecteurMysql;
import com.bd.dao.LivreDao;
import com.bd.entity.Categorie;
import com.bd.entity.Livre;
import com.bd.exceptions.DaoException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestLivreDao {

    private static ConnecteurMysql connecteur;
    private static Connection connection;
    private static LivreDao livreDao;
    private static CategorieDao categorieDao;
    
    private static int testLivreId;
    private static final String testLivreTitre = "Titre du Livre de Test JUnit";
    private static int testCategorieId;

    @BeforeAll
    public static void setUp() throws DaoException {
        connecteur = new ConnecteurMysql();
        connection = connecteur.getConnection();
        livreDao = new LivreDao(connection);
        categorieDao = new CategorieDao(connection);
        
        System.out.println("--- Démarrage des tests LivreDao ---");

        // Nettoyage initial
        Livre existingLivre = livreDao.getLivreByTitre(testLivreTitre);
        if (existingLivre != null) {
            livreDao.deleteLivre(existingLivre.getId_livre());
        }
        
        // Assurer l'existence d'une catégorie pour le test
        Categorie cat = categorieDao.getCategorieByNom("Roman Test");
        if (cat == null) {
            cat = categorieDao.addCategorie(new Categorie("Roman Test"));
        }
        testCategorieId = cat.getId_categorie();
    }

    @Test
    @Order(1)
    public void testAddLivre() throws DaoException {
        System.out.println("\n--- Test d'ajout d'un livre ---");
        Livre newLivre = new Livre(testLivreTitre, "Auteur Test", "Description test.", 
                                   new BigDecimal("25.50"), "url.jpg", "uri.pdf", 
                                   LocalDate.now(), testCategorieId);
        
        Livre addedLivre = livreDao.addLivre(newLivre);
        
        assertNotNull(addedLivre);
        assertTrue(addedLivre.getId_livre() > 0);
        testLivreId = addedLivre.getId_livre();
        System.out.println("Livre ajouté avec succès : " + addedLivre);
    }
    
    @Test
    @Order(2)
    public void testGetLivre() throws DaoException {
        System.out.println("\n--- Test de récupération d'un livre ---");
        Livre foundById = livreDao.getLivreById(testLivreId);
        Livre foundByTitre = livreDao.getLivreByTitre(testLivreTitre);
        
        assertNotNull(foundById, "Le livre doit être trouvé par son ID.");
        assertNotNull(foundByTitre, "Le livre doit être trouvé par son titre.");
        assertEquals(testLivreId, foundById.getId_livre());
        System.out.println("Livre trouvé par ID : " + foundById);
    }

    @Test
    @Order(3)
    public void testUpdateLivre() throws DaoException {
        System.out.println("\n--- Test de mise à jour d'un livre ---");
        Livre livreToUpdate = livreDao.getLivreById(testLivreId);
        livreToUpdate.setPrix(new BigDecimal("29.99"));
        
        boolean updated = livreDao.updateLivre(livreToUpdate);
        assertTrue(updated, "La mise à jour doit retourner true.");
        
        Livre checkedLivre = livreDao.getLivreById(testLivreId);
        // Utiliser compareTo pour les BigDecimal
        assertTrue(new BigDecimal("29.99").compareTo(checkedLivre.getPrix()) == 0, "Le prix doit être mis à jour.");
        System.out.println("Livre mis à jour avec succès.");
    }

    @Test
    @Order(4)
    public void testDeleteLivre() throws DaoException {
        System.out.println("\n--- Test de suppression d'un livre ---");
        boolean deleted = livreDao.deleteLivre(testLivreId);
        assertTrue(deleted, "La suppression doit retourner true.");
        
        Livre shouldBeNull = livreDao.getLivreById(testLivreId);
        assertNull(shouldBeNull, "Le livre ne doit plus exister.");
        System.out.println("Livre supprimé avec succès.");
    }

    @AfterAll
    public static void tearDown() throws DaoException {
        if (connecteur != null) {
            connecteur.close();
        }
        System.out.println("\n--- Tests LivreDao terminés. ---");
    }
}