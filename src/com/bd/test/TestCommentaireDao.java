// src/com/bd/test/TestCommentaireDao.java
package com.bd.test;

import com.bd.dao.CommentaireDao;
import com.bd.dao.ConnecteurMysql;
import com.bd.dao.LivreDao;
import com.bd.dao.UtilisateurDao;
import com.bd.entity.Commentaire;
import com.bd.entity.Livre;
import com.bd.entity.Utilisateur;
import com.bd.exceptions.DaoException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public class TestCommentaireDao {

    public static void main(String[] args) {
        ConnecteurMysql connecteur = null;
        Connection connection = null;
        UtilisateurDao utilisateurDao = null;
        LivreDao livreDao = null;
        CommentaireDao commentaireDao = null;

        int testUserId = 999990;
        int testLivreId = 888880;
        int testCommentaireId = 777770;

        try {
            connecteur = new ConnecteurMysql();
            connection = connecteur.getConnection();

            commentaireDao = new CommentaireDao(connection);
            utilisateurDao = new UtilisateurDao(connection);
            livreDao = new LivreDao(connection);

            System.out.println("--- Démarrage des tests CommentaireDao ---");

            System.out.println("\n--- Nettoyage initial ---");
            if (commentaireDao.getCommentaireById(testCommentaireId) != null) {
                commentaireDao.deleteCommentaire(testCommentaireId);
                System.out.println("Commentaire de test avec ID " + testCommentaireId + " supprimé.");
            }
            if (utilisateurDao.getUtilisateurById(testUserId) != null) {
                utilisateurDao.deleteUtilisateur(testUserId);
                System.out.println("Utilisateur de test avec ID " + testUserId + " supprimé.");
            }
            if (livreDao.getLivreById(testLivreId) != null) {
                livreDao.deleteLivre(testLivreId);
                System.out.println("Livre de test avec ID " + testLivreId + " supprimé.");
            }

            System.out.println("\n--- Préparation des données de test ---");
            Utilisateur testUser = new Utilisateur("NomComTest", "PrenomComTest", "test.com.user@example.com", "mdpComTest", "adresseComTest", "villeComTest", "12345", "France", LocalDate.of(1991, 2, 2), "client");
            testUser = utilisateurDao.addUtilisateur(testUser);
            if (testUser == null || testUser.getId_utilisateur() == 0) {
                 System.err.println("Échec de la création de l'utilisateur de test. Impossible de continuer les tests de commentaire.");
                 return;
            }
            System.out.println("Utilisateur de test créé/trouvé: " + testUser);

            Livre testLivre = new Livre("Titre Test Com", "Auteur Test Com", "Description pour le test de commentaire.", BigDecimal.valueOf(19.99), "url_image_test", "url_fichier_test", LocalDate.now(), 1);
            testLivre = livreDao.addLivre(testLivre);
            if (testLivre == null || testLivre.getId_livre() == 0) {
                System.err.println("Échec de la création du livre de test. Impossible de continuer les tests de commentaire.");
                return;
            }
            System.out.println("Livre de test créé/trouvé: " + testLivre);


            System.out.println("\n--- Test d'ajout d'un commentaire ---");
            Commentaire newCommentaire = new Commentaire(testUser.getId_utilisateur(), testLivre.getId_livre(), "Excellent livre, très bien écrit !", 5);
            Commentaire addedCommentaire = commentaireDao.addCommentaire(newCommentaire);

            if (addedCommentaire != null && addedCommentaire.getId_commentaire() != 0) {
                System.out.println("Commentaire ajouté avec succès : " + addedCommentaire);
            } else {
                System.out.println("Échec de l'ajout du commentaire.");
            }

            System.out.println("\n--- Test de recherche par ID ---");
            if (addedCommentaire != null && addedCommentaire.getId_commentaire() != 0) {
                Commentaire foundCommentaire = commentaireDao.getCommentaireById(addedCommentaire.getId_commentaire());
                if (foundCommentaire != null) {
                    System.out.println("Commentaire trouvé par ID : " + foundCommentaire);
                } else {
                    System.out.println("Commentaire non trouvé par ID " + addedCommentaire.getId_commentaire());
                }
            }

            System.out.println("\n--- Test de récupération des commentaires par livre ---");
            List<Commentaire> livreCommentaires = commentaireDao.getCommentairesByLivre(testLivre.getId_livre());
            System.out.println("Nombre de commentaires pour le livre " + testLivre.getId_livre() + " : " + livreCommentaires.size());
            for (Commentaire cmt : livreCommentaires) {
                System.out.println(cmt);
            }

            System.out.println("\n--- Test de récupération des commentaires par utilisateur ---");
            List<Commentaire> userCommentaires = commentaireDao.getCommentairesByUtilisateur(testUser.getId_utilisateur());
            System.out.println("Nombre de commentaires pour l'utilisateur " + testUser.getId_utilisateur() + " : " + userCommentaires.size());
            for (Commentaire cmt : userCommentaires) {
                System.out.println(cmt);
            }

            System.out.println("\n--- Test de récupération de tous les commentaires ---");
            List<Commentaire> allCommentaires = commentaireDao.getAllCommentaires();
            System.out.println("Nombre total de commentaires : " + allCommentaires.size());
            for (Commentaire cmt : allCommentaires) {
                System.out.println(cmt);
            }

            System.out.println("\n--- Test de mise à jour d'un commentaire ---");
            if (addedCommentaire != null && addedCommentaire.getId_commentaire() != 0) {
                addedCommentaire.setNote(4);
                addedCommentaire.setText_commentaire("Livre très intéressant, mais quelques passages longs.");
                boolean updated = commentaireDao.updateCommentaire(addedCommentaire);
                System.out.println("Commentaire mis à jour : " + (updated ? "SUCCÈS" : "ÉCHEC"));
                if (updated) {
                    Commentaire checkUpdated = commentaireDao.getCommentaireById(addedCommentaire.getId_commentaire());
                    System.out.println("Vérification : " + checkUpdated);
                }
            }

            System.out.println("\n--- Test de suppression d'un commentaire ---");
            if (addedCommentaire != null && addedCommentaire.getId_commentaire() != 0) {
                boolean deleted = commentaireDao.deleteCommentaire(addedCommentaire.getId_commentaire());
                System.out.println("Commentaire supprimé : " + (deleted ? "SUCCÈS" : "ÉCHEC"));
                if (deleted) {
                    System.out.println("Vérification : Commentaire avec ID " + addedCommentaire.getId_commentaire() + " existe toujours ? " + (commentaireDao.getCommentaireById(addedCommentaire.getId_commentaire()) != null ? "Oui" : "Non (Correct)"));
                }
            }

            System.out.println("\n--- Fin des tests CommentaireDao ---");

        } catch (DaoException e) {
            System.err.println("!!!! Une erreur DAO s'est produite lors des tests Commentaire !!!!");
            System.err.println("Message de l'erreur : " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("\n--- Nettoyage final des données de test ---");
            try {
                if (commentaireDao != null && commentaireDao.commentaireExists(testCommentaireId)) {
                    commentaireDao.deleteCommentaire(testCommentaireId);
                    System.out.println("Commentaire de test avec ID " + testCommentaireId + " supprimé.");
                }
                if (utilisateurDao != null && utilisateurDao.utilisateurExists(testUserId)) {
                    utilisateurDao.deleteUtilisateur(testUserId);
                    System.out.println("Utilisateur de test avec ID " + testUserId + " supprimé.");
                }
                if (livreDao != null && livreDao.livreExists(testLivreId)) {
                    livreDao.deleteLivre(testLivreId);
                    System.out.println("Livre de test avec ID " + testLivreId + " supprimé.");
                }
            } catch (DaoException e) {
                System.err.println("Erreur lors du nettoyage final des données de test : " + e.getMessage());
                e.printStackTrace();
            }

            if (connecteur != null) {
                try {
                    connecteur.closeConnection();
                } catch (DaoException e) {
                    System.err.println("Erreur lors de la fermeture de la connexion du connecteur dans TestCommentaireDao : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}