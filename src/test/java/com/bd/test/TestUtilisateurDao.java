package com.bd.test;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.bd.dao.ConnecteurMysql;
import com.bd.dao.UtilisateurDao;
import com.bd.entity.Utilisateur;
import com.bd.exceptions.DaoException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestUtilisateurDao {

    private static ConnecteurMysql connecteur;
    private static Connection connection;
    private static UtilisateurDao utilisateurDao;

    private static int testUserId;
    private static final String testUserEmail = "test.utilisateur.dao@example.com";

    @BeforeAll
    public static void setUp() throws DaoException {
        connecteur = new ConnecteurMysql();
        connection = connecteur.getConnection();
        utilisateurDao = new UtilisateurDao(connection);

        System.out.println("--- Démarrage des tests UtilisateurDao ---");

        // Nettoyage initial
        Utilisateur existingUser = utilisateurDao.getUtilisateurByEmail(testUserEmail);
        if (existingUser != null) {
            utilisateurDao.deleteUtilisateur(existingUser.getId_utilisateur());
        }
    }

    @Test
    @Order(1)
    public void testAddUtilisateur() throws DaoException {
        System.out.println("\n--- Test d'ajout d'un utilisateur ---");
        Utilisateur newUser = new Utilisateur("NomTest", "PrenomTest", testUserEmail, "motdepassehache", 
                                          "123 Rue de Java", "Codetown", "75000", "France", 
                                          LocalDate.now(), "client");
        
        Utilisateur addedUser = utilisateurDao.addUtilisateur(newUser);

        assertNotNull(addedUser);
        assertTrue(addedUser.getId_utilisateur() > 0);
        testUserId = addedUser.getId_utilisateur();
        System.out.println("Utilisateur ajouté avec succès : " + addedUser);
    }

    @Test
    @Order(2)
    public void testGetUtilisateur() throws DaoException {
        System.out.println("\n--- Test de récupération d'un utilisateur ---");
        Utilisateur foundById = utilisateurDao.getUtilisateurById(testUserId);
        Utilisateur foundByEmail = utilisateurDao.getUtilisateurByEmail(testUserEmail);
        
        assertNotNull(foundById, "L'utilisateur doit être trouvé par son ID.");
        assertNotNull(foundByEmail, "L'utilisateur doit être trouvé par son email.");
        assertEquals(testUserEmail, foundById.getEmail());
        System.out.println("Utilisateur trouvé avec succès.");
    }

    @Test
    @Order(3)
    public void testUpdateUtilisateur() throws DaoException {
        System.out.println("\n--- Test de mise à jour d'un utilisateur ---");
        Utilisateur userToUpdate = utilisateurDao.getUtilisateurById(testUserId);
        userToUpdate.setVille("NouvelleVille");
        userToUpdate.setRole("admin");
        
        boolean updated = utilisateurDao.updateUtilisateur(userToUpdate);
        assertTrue(updated);
        
        Utilisateur checkedUser = utilisateurDao.getUtilisateurById(testUserId);
        assertEquals("NouvelleVille", checkedUser.getVille());
        assertEquals("admin", checkedUser.getRole());
        System.out.println("Utilisateur mis à jour avec succès.");
    }

    @Test
    @Order(4)
    public void testDeleteUtilisateur() throws DaoException {
        System.out.println("\n--- Test de suppression d'un utilisateur ---");
        boolean deleted = utilisateurDao.deleteUtilisateur(testUserId);
        assertTrue(deleted);
        
        Utilisateur shouldBeNull = utilisateurDao.getUtilisateurById(testUserId);
        assertNull(shouldBeNull);
        System.out.println("Utilisateur supprimé avec succès.");
    }

    @AfterAll
    public static void tearDown() throws DaoException {
        if (connecteur != null) {
            connecteur.close();
        }
        System.out.println("\n--- Tests UtilisateurDao terminés. ---");
    }
}