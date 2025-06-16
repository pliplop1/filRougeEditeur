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
        String testUserEmail = "test.com.user@example.com"; // Email pour l'utilisateur de test
        int testLivreId = 888880;
        String testLivreTitre = "Livre de Test Commentaire"; // Titre pour le livre de test
        int testCommentaireId = 777770; // Cet ID sera utilisé si vous voulez tenter de retrouver un commentaire spécifique, mais l'ID est auto-incrémenté normalement

        try {
            connecteur = new ConnecteurMysql();
            connection = connecteur.getConnection();

            commentaireDao = new CommentaireDao(connection);
            utilisateurDao = new UtilisateurDao(connection);
            livreDao = new LivreDao(connection);

            System.out.println("--- Démarrage des tests CommentaireDao ---");

            System.out.println("\n--- Nettoyage initial ---");

            // *** Nettoyage de l'utilisateur de test par email ***
            Utilisateur existingUserByEmail = utilisateurDao.getUtilisateurByEmail(testUserEmail);
            if (existingUserByEmail != null) {
                utilisateurDao.deleteUtilisateur(existingUserByEmail.getId_utilisateur());
                System.out.println("Utilisateur de test existant avec email '" + testUserEmail + "' supprimé.");
            } else {
                System.out.println("Aucun utilisateur de test existant avec email '" + testUserEmail + "' à supprimer.");
            }

            // Nettoyage par ID, si l'ID est connu et qu'il n'a pas été supprimé par l'email
            if (utilisateurDao.utilisateurExists(testUserId)) {
                utilisateurDao.deleteUtilisateur(testUserId);
                System.out.println("Utilisateur de test avec ID " + testUserId + " supprimé (potentiellement redondant mais sécuritaire).");
            } else {
                System.out.println("Aucun utilisateur de test avec ID " + testUserId + " à supprimer.");
            }

            // *** Nettoyage du livre de test par titre ***
            Livre existingLivreByTitre = livreDao.getLivreByTitre(testLivreTitre);
            if (existingLivreByTitre != null) {
                // Avant de supprimer le livre, il faut s'assurer qu'aucun commentaire n'y est lié
                List<Commentaire> commentairesLieesAuLivre = commentaireDao.getCommentairesByLivreId(existingLivreByTitre.getId_livre());
                for (Commentaire c : commentairesLieesAuLivre) {
                    commentaireDao.deleteCommentaire(c.getId_commentaire());
                    System.out.println("Commentaire lié au livre " + existingLivreByTitre.getId_livre() + " supprimé.");
                }
                livreDao.deleteLivre(existingLivreByTitre.getId_livre());
                System.out.println("Livre de test existant avec titre '" + testLivreTitre + "' supprimé.");
            } else {
                System.out.println("Aucun livre de test existant avec titre '" + testLivreTitre + "' à supprimer.");
            }


            // Nettoyage par ID, si l'ID est connu et qu'il n'a pas été supprimé par le titre
            if (livreDao.livreExists(testLivreId)) {
                // Avant de supprimer le livre, il faut s'assurer qu'aucun commentaire n'y est lié
                List<Commentaire> commentairesLieesAuLivre = commentaireDao.getCommentairesByLivreId(testLivreId);
                for (Commentaire c : commentairesLieesAuLivre) {
                    commentaireDao.deleteCommentaire(c.getId_commentaire());
                    System.out.println("Commentaire lié au livre " + testLivreId + " supprimé.");
                }
                livreDao.deleteLivre(testLivreId);
                System.out.println("Livre de test avec ID " + testLivreId + " supprimé (potentiellement redondant mais sécuritaire).");
            } else {
                System.out.println("Aucun livre de test avec ID " + testLivreId + " à supprimer.");
            }

            // Nettoyage de commentaires spécifiques si l'ID de test est utilisé (moins courant pour l'auto-incrément)
            if (commentaireDao.commentaireExists(testCommentaireId)) {
                commentaireDao.deleteCommentaire(testCommentaireId);
                System.out.println("Commentaire de test avec ID " + testCommentaireId + " supprimé.");
            } else {
                System.out.println("Aucun commentaire de test avec ID " + testCommentaireId + " à supprimer.");
            }


            System.out.println("\n--- Préparation des données de test ---");

            // Ajout de l'utilisateur de test
            Utilisateur utilisateur = new Utilisateur(testUserId, "NomTestCom", "PrenomTestCom", testUserEmail,
                    "passwordHash123", "123 Rue Test", "Testville", "12345", "France",
                    LocalDate.now(), "client");
            utilisateurDao.addUtilisateur(utilisateur);
            System.out.println("Utilisateur de test ajouté: " + utilisateur);

            // Ajout d'un livre de test (nécessaire pour un commentaire)
            Livre livre = new Livre(testLivreId, testLivreTitre, "Auteur Test Com", "Descriptif Livre Test Com",
                    new BigDecimal("19.99"), "url_image_com.jpg", "uri_fichier_com.pdf",
                    LocalDate.of(2023, 1, 1), 1); // Assurez-vous que la catégorie 1 existe ou créez-en une
            livreDao.addLivre(livre);
            System.out.println("Livre de test ajouté: " + livre);

            // --- Test d'ajout de commentaire ---
            System.out.println("\n--- Test d'ajout de commentaire ---");
            Commentaire nouveauCommentaire = new Commentaire(utilisateur.getId_utilisateur(), livre.getId_livre(),
                    "Super livre, j'ai adoré !", 5);
            commentaireDao.addCommentaire(nouveauCommentaire);
            System.out.println("Commentaire ajouté: " + nouveauCommentaire);

            // Récupérer le commentaire pour vérifier l'ID généré
            Commentaire commentaireRecupere = commentaireDao.getCommentaireById(nouveauCommentaire.getId_commentaire());
            if (commentaireRecupere != null) {
                System.out.println("Commentaire récupéré après ajout: " + commentaireRecupere);
            } else {
                System.err.println("Échec de la récupération du commentaire ajouté.");
            }

            // --- Test de récupération de commentaires par livre ---
            System.out.println("\n--- Test de récupération des commentaires par livre ---");
            List<Commentaire> commentairesDuLivre = commentaireDao.getCommentairesByLivreId(livre.getId_livre());
            if (!commentairesDuLivre.isEmpty()) {
                System.out.println("Commentaires pour le livre '" + livre.getTitre() + "':");
                for (Commentaire c : commentairesDuLivre) {
                    System.out.println(" - " + c);
                }
            } else {
                System.out.println("Aucun commentaire trouvé pour le livre '" + livre.getTitre() + "'.");
            }

            // --- Test de mise à jour de commentaire ---
            System.out.println("\n--- Test de mise à jour de commentaire ---");
            if (commentaireRecupere != null) {
                commentaireRecupere.setText_commentaire("Livre excellent, note maximale !");
                commentaireRecupere.setNote(5);
                boolean updated = commentaireDao.updateCommentaire(commentaireRecupere);
                System.out.println("Commentaire mis à jour ? " + (updated ? "Oui" : "Non"));
                commentaireRecupere = commentaireDao.getCommentaireById(commentaireRecupere.getId_commentaire());
                System.out.println("Commentaire après mise à jour: " + commentaireRecupere);
            } else {
                System.err.println("Impossible de tester la mise à jour, commentaire non récupéré.");
            }

            // --- Test de suppression de commentaire ---
            System.out.println("\n--- Test de suppression de commentaire ---");
            if (commentaireRecupere != null) {
                boolean deleted = commentaireDao.deleteCommentaire(commentaireRecupere.getId_commentaire());
                System.out.println("Commentaire supprimé ? " + (deleted ? "Oui" : "Non"));
                System.out.println("Vérification : Commentaire avec ID " + commentaireRecupere.getId_commentaire() + " existe toujours ? " + (commentaireDao.getCommentaireById(commentaireRecupere.getId_commentaire()) != null ? "Oui" : "Non (Correct)"));
            } else {
                System.err.println("Impossible de tester la suppression, commentaire non récupéré.");
            }

            System.out.println("\n--- Fin des tests CommentaireDao ---");

        } catch (DaoException e) {
            System.err.println("!!!! Une erreur DAO s'est produite lors des tests Commentaire !!!!");
            System.err.println("Message de l'erreur : " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("\n--- Nettoyage final des données de test ---\n");
            try {
                // Nettoyage de l'utilisateur, du livre et du commentaire créés pour le test
                // Vérifier l'existence avant de tenter de supprimer
                
                // Supprimer les commentaires liés au livre de test d'abord
                try {
                    if (livreDao != null && livreDao.getLivreByTitre(testLivreTitre) != null) {
                        int livreIdToDelete = livreDao.getLivreByTitre(testLivreTitre).getId_livre();
                        List<Commentaire> commentsToDelete = commentaireDao.getCommentairesByLivreId(livreIdToDelete);
                        for (Commentaire c : commentsToDelete) {
                            if (commentaireDao.commentaireExists(c.getId_commentaire())) {
                                commentaireDao.deleteCommentaire(c.getId_commentaire());
                                System.out.println("Commentaire avec ID " + c.getId_commentaire() + " supprimé lors du nettoyage final.");
                            }
                        }
                    }
                } catch (DaoException e) {
                    System.err.println("Erreur lors du nettoyage des commentaires liés au livre de test: " + e.getMessage());
                }

                if (livreDao != null) {
                    Livre existingLivre = livreDao.getLivreByTitre(testLivreTitre);
                    if (existingLivre != null) {
                        livreDao.deleteLivre(existingLivre.getId_livre());
                        System.out.println("Livre de test avec titre '" + testLivreTitre + "' supprimé (nettoyage final).");
                    }
                }

                if (utilisateurDao != null) {
                    Utilisateur existingUser = utilisateurDao.getUtilisateurByEmail(testUserEmail);
                    if (existingUser != null) {
                        utilisateurDao.deleteUtilisateur(existingUser.getId_utilisateur());
                        System.out.println("Utilisateur de test avec email '" + testUserEmail + "' supprimé (nettoyage final).");
                    }
                }

                // Assurez-vous que l'ID de test direct est également nettoyé si jamais il a été utilisé ou laissé
                if (commentaireDao != null && commentaireDao.commentaireExists(testCommentaireId)) {
                    commentaireDao.deleteCommentaire(testCommentaireId);
                    System.out.println("Commentaire de test avec ID " + testCommentaireId + " supprimé (nettoyage final direct).");
                }


            } catch (DaoException e) {
                System.err.println("Erreur lors du nettoyage final des données de test : " + e.getMessage());
                e.printStackTrace();
            } finally {
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
}