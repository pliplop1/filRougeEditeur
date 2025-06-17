package com.bd.test;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.bd.dao.CommandeDao;
import com.bd.dao.ConnecteurMysql;
import com.bd.dao.UtilisateurDao;
import com.bd.entity.Commande;
import com.bd.entity.Utilisateur;
import com.bd.exceptions.DaoException;

public class TestCommandeDao {

    private static ConnecteurMysql connecteur;
    private static Connection connection;
    private static UtilisateurDao utilisateurDao;
    private static CommandeDao commandeDao;
    
    private static Utilisateur testUser;
    private static final String testUserEmail = "test.commande.user@example.com";

    // Exécuté une fois avant tous les tests
    @BeforeAll
    public static void setUp() throws DaoException {
        connecteur = new ConnecteurMysql();
        connection = connecteur.getConnection();
        utilisateurDao = new UtilisateurDao(connection);
        commandeDao = new CommandeDao(connection);

        // Nettoyage et création de l'utilisateur de test
        Utilisateur existingUser = utilisateurDao.getUtilisateurByEmail(testUserEmail);
        if (existingUser != null) {
            // S'il y a des commandes existantes pour cet utilisateur, les supprimer d'abord
            List<Commande> oldCommandes = commandeDao.getCommandesByUtilisateur(existingUser.getId_utilisateur());
            for(Commande cmd : oldCommandes) {
                commandeDao.deleteCommande(cmd.getId_commande());
            }
            utilisateurDao.deleteUtilisateur(existingUser.getId_utilisateur());
        }
        
        testUser = new Utilisateur(
            "NomCmdTest", "PrenomCmdTest", testUserEmail,
            "motdepassehache", "123 Rue de la Commande", "VilleCommande", "75001",
            "France", LocalDate.now(), "client"
        );
        testUser = utilisateurDao.addUtilisateur(testUser);
        System.out.println("Utilisateur de test créé : " + testUser);
    }

    // Un seul test qui exécute toutes les étapes dans l'ordre
    @Test
    public void testCommandeCrudWorkflow() throws DaoException {
        
        // --- 1. Test d'Ajout ---
        System.out.println("\n--- Test d'ajout de commande ---");
        Commande newCommande = new Commande(testUser.getId_utilisateur(), new BigDecimal("150.75"));
        Commande addedCommande = commandeDao.addCommande(newCommande);
        
        assertNotNull(addedCommande, "La commande ajoutée ne doit pas être nulle.");
        assertTrue(addedCommande.getId_commande() > 0, "L'ID de la commande doit être positif.");
        int newCommandeId = addedCommande.getId_commande(); // On sauvegarde l'ID
        System.out.println("Commande ajoutée avec succès : " + addedCommande);

        // --- 2. Test de Récupération par ID ---
        System.out.println("\n--- Test de récupération par ID ---");
        Commande foundCommande = commandeDao.getCommandeById(newCommandeId);
        assertNotNull(foundCommande, "La commande doit être trouvée par son ID.");
        assertEquals(newCommandeId, foundCommande.getId_commande());
        System.out.println("Commande trouvée par ID : " + foundCommande);
        
        // --- 3. Test de Récupération par Utilisateur ---
        System.out.println("\n--- Test de récupération par utilisateur ---");
        List<Commande> userCommandes = commandeDao.getCommandesByUtilisateur(testUser.getId_utilisateur());
        assertNotNull(userCommandes);
        assertEquals(1, userCommandes.size());
        System.out.println("Une commande trouvée pour l'utilisateur.");
        
        // --- 4. Test de Mise à Jour ---
        System.out.println("\n--- Test de mise à jour de commande ---");
        foundCommande.setStatut("expediee");
        boolean updated = commandeDao.updateCommande(foundCommande);
        assertTrue(updated, "La mise à jour doit retourner true.");
        
        Commande checkedCommande = commandeDao.getCommandeById(newCommandeId);
        assertEquals("expediee", checkedCommande.getStatut());
        System.out.println("Statut de la commande mis à jour avec succès.");

        // --- 5. Test de Suppression ---
        System.out.println("\n--- Test de suppression de commande ---");
        boolean deleted = commandeDao.deleteCommande(newCommandeId);
        assertTrue(deleted, "La suppression doit retourner true.");
        
        Commande shouldBeNull = commandeDao.getCommandeById(newCommandeId);
        assertNull(shouldBeNull, "La commande ne doit plus exister.");
        System.out.println("Commande supprimée avec succès.");
    }

    // Exécuté une fois après tous les tests
    @AfterAll
    public static void tearDown() throws DaoException {
        // Nettoyage final de l'utilisateur de test
        if (utilisateurDao != null && testUser != null) {
            utilisateurDao.deleteUtilisateur(testUser.getId_utilisateur());
            System.out.println("\nUtilisateur de test supprimé.");
        }
        
        if (connecteur != null) {
            connecteur.close();
            System.out.println("Connexion à la base de données fermée.");
        }
    }
}