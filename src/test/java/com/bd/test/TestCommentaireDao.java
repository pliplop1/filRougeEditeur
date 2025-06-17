package com.bd.test;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.bd.dao.CategorieDao;
import com.bd.dao.CommentaireDao;
import com.bd.dao.ConnecteurMysql;
import com.bd.dao.LivreDao;
import com.bd.dao.UtilisateurDao;
import com.bd.entity.Categorie;
import com.bd.entity.Commentaire;
import com.bd.entity.Livre;
import com.bd.entity.Utilisateur;
import com.bd.exceptions.DaoException;

public class TestCommentaireDao {

    private static ConnecteurMysql connecteur;
    private static Connection connection;
    private static UtilisateurDao utilisateurDao;
    private static LivreDao livreDao;
    private static CommentaireDao commentaireDao;
    private static CategorieDao categorieDao;

    private static Utilisateur testUser;
    private static Livre testLivre;
    private static final String testUserEmail = "test.commentaire.user@example.com";
    private static final String testLivreTitre = "Livre Pour Commentaire";

    @BeforeAll
    public static void setUp() throws DaoException {
        connecteur = new ConnecteurMysql();
        connection = connecteur.getConnection();
        utilisateurDao = new UtilisateurDao(connection);
        livreDao = new LivreDao(connection);
        commentaireDao = new CommentaireDao(connection);
        categorieDao = new CategorieDao(connection);

        System.out.println("--- Démarrage des tests CommentaireDao ---");

        // Nettoyage et création des dépendances (Utilisateur, Categorie, Livre)
        // Supprimer l'utilisateur de test s'il existe (cascade supprime ses commentaires)
        Utilisateur existingUser = utilisateurDao.getUtilisateurByEmail(testUserEmail);
        if (existingUser != null) {
            utilisateurDao.deleteUtilisateur(existingUser.getId_utilisateur());
        }

        // Supprimer le livre de test s'il existe (cascade supprime ses commentaires)
        Livre existingLivre = livreDao.getLivreByTitre(testLivreTitre);
        if (existingLivre != null) {
            livreDao.deleteLivre(existingLivre.getId_livre());
        }

        // Créer une catégorie de test si elle n'existe pas
        Categorie cat = categorieDao.getCategorieByNom("Test");
        if (cat == null) {
            cat = categorieDao.addCategorie(new Categorie("Test"));
        }

        // Créer l'utilisateur et le livre de test
        testUser = new Utilisateur("NomCom", "PrenomCom", testUserEmail, "pass", "addr", "ville", "cp", "pays", LocalDate.now(), "client");
        testUser = utilisateurDao.addUtilisateur(testUser);

        testLivre = new Livre(testLivreTitre, "Auteur Com", "Desc", new BigDecimal("10.0"), "url", "uri", LocalDate.now(), cat.getId_categorie());
        testLivre = livreDao.addLivre(testLivre);
        
        System.out.println("Données de test créées (Utilisateur et Livre).");
    }

    @Test
    public void testCommentaireCrudWorkflow() throws DaoException {
        // --- 1. Test d'Ajout ---
        System.out.println("\n--- Test d'ajout de commentaire ---");
        Commentaire newCommentaire = new Commentaire(testUser.getId_utilisateur(), testLivre.getId_livre(), "Excellent livre !", 5);
        Commentaire addedCommentaire = commentaireDao.addCommentaire(newCommentaire);

        assertNotNull(addedCommentaire, "Le commentaire ajouté ne doit pas être nul.");
        assertTrue(addedCommentaire.getId_commentaire() > 0, "L'ID du commentaire doit être positif.");
        int newCommentaireId = addedCommentaire.getId_commentaire();
        System.out.println("Commentaire ajouté avec succès : " + addedCommentaire);

        // --- 2. Test de Récupération par ID ---
        System.out.println("\n--- Test de récupération par ID ---");
        Commentaire foundCommentaire = commentaireDao.getCommentaireById(newCommentaireId);
        assertNotNull(foundCommentaire, "Le commentaire doit être trouvé par son ID.");
        assertEquals("Excellent livre !", foundCommentaire.getText_commentaire());

        // --- 3. Test de Récupération par Livre ---
        System.out.println("\n--- Test de récupération par livre ---");
        List<Commentaire> commentairesDuLivre = commentaireDao.getCommentairesByLivreId(testLivre.getId_livre());
        assertEquals(1, commentairesDuLivre.size(), "Il doit y avoir un commentaire pour ce livre.");

        // --- 4. Test de Mise à Jour ---
        System.out.println("\n--- Test de mise à jour ---");
        foundCommentaire.setText_commentaire("Finalement, un peu décevant.");
        foundCommentaire.setNote(3);
        boolean updated = commentaireDao.updateCommentaire(foundCommentaire);
        assertTrue(updated, "La mise à jour doit retourner true.");
        
        Commentaire checkedCommentaire = commentaireDao.getCommentaireById(newCommentaireId);
        assertEquals("Finalement, un peu décevant.", checkedCommentaire.getText_commentaire());
        assertEquals(3, checkedCommentaire.getNote());
        System.out.println("Commentaire mis à jour avec succès.");
        
        // --- 5. Test de Suppression ---
        System.out.println("\n--- Test de suppression ---");
        boolean deleted = commentaireDao.deleteCommentaire(newCommentaireId);
        assertTrue(deleted, "La suppression doit retourner true.");
        
        Commentaire shouldBeNull = commentaireDao.getCommentaireById(newCommentaireId);
        assertNull(shouldBeNull, "Le commentaire ne doit plus exister.");
        System.out.println("Commentaire supprimé avec succès.");
    }

    @AfterAll
    public static void tearDown() throws DaoException {
        // Le nettoyage se fait en cascade grâce aux contraintes de la BDD
        // mais on supprime manuellement pour être propre
        if (utilisateurDao != null && testUser != null) {
            utilisateurDao.deleteUtilisateur(testUser.getId_utilisateur());
        }
        if (livreDao != null && testLivre != null) {
            livreDao.deleteLivre(testLivre.getId_livre());
        }
        
        if (connecteur != null) {
            connecteur.close();
        }
        System.out.println("\n--- Tests CommentaireDao terminés et données nettoyées. ---");
    }
}