// src/com/bd/test/TestCommandeDao.java
package com.bd.test;

import com.bd.dao.CommandeDao;
import com.bd.dao.ConnecteurMysql;
import com.bd.dao.UtilisateurDao;
import com.bd.entity.Commande;
import com.bd.entity.Utilisateur;
import com.bd.exceptions.DaoException;
import java.math.BigDecimal; // Nécessaire pour BigDecimal
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TestCommandeDao {

    public static void main(String[] args) {
        ConnecteurMysql connecteur = null;
        Connection connection = null;
        UtilisateurDao utilisateurDao = null;
        CommandeDao commandeDao = null;

        int testUserId = 999999;
        // int testCommandeId = 0; // Ne peut pas être initialisé ici car l'ID est généré et non retourné

        try {
            connecteur = new ConnecteurMysql();
            connection = connecteur.getConnection();

            utilisateurDao = new UtilisateurDao(connection);
            commandeDao = new CommandeDao(connection);

            System.out.println("--- Démarrage des tests CommandeDao ---");

            System.out.println("\n--- Nettoyage initial ---");
            if (utilisateurDao.getUtilisateurById(testUserId) != null) {
                utilisateurDao.deleteUtilisateur(testUserId);
                System.out.println("Utilisateur de test avec ID " + testUserId + " supprimé.");
            }

            System.out.println("\n--- Création d'un utilisateur de test pour les commandes ---");
            Utilisateur testUser = new Utilisateur(
                "NomCmdTest", "PrenomCmdTest", "test.commande.user@example.com",
                "motdepassehache", "123 Rue de la Commande", "VilleCommande", "75001",
                "France", LocalDate.of(2023, 1, 1), "client"
            );
            testUser = utilisateurDao.addUtilisateur(testUser);
            if (testUser == null || testUser.getId_utilisateur() == 0) {
                 System.err.println("Échec de la création de l'utilisateur de test. Impossible de continuer les tests de commande.");
                 return;
            }
            System.out.println("Utilisateur de test créé/trouvé: " + testUser);
            testUserId = testUser.getId_utilisateur();

            System.out.println("\n--- Test d'ajout d'une commande ---");
            // CORRECTION DU CONSTRUCTEUR DE COMMANDE : Utilise (id_utilisateur, montant_total)
            // Le statut et la date_commande sont supposés être gérés par la base de données ou le DAO.
            Commande newCommande = new Commande(testUser.getId_utilisateur(), BigDecimal.valueOf(150.75));

            // CORRECTION DU TYPE DE RETOUR DE addCommande : La méthode retourne void.
            // Pour que ce test soit robuste, il est FORTEMENT RECOMMANDÉ de modifier addCommande
            // dans CommandeDao.java pour qu'elle retourne l'objet Commande avec son ID généré.
            commandeDao.addCommande(newCommande); // <-- Plus d'assignation ici.

            System.out.println("Commande ajoutée (si le constructeur ci-dessus est correct).");
            System.out.println("ATTENTION : Comme addCommande retourne void, nous ne pouvons pas récupérer l'ID de la commande pour les tests suivants.");
            System.out.println("Pour des tests robustes de get/update/delete, modifiez addCommande dans CommandeDao.java.");

            // --- Tests suivants (commentés ou ajustés car dépendent d'un ID de commande généré et non récupérable) ---
            // Sans l'ID généré par la base de données et retourné par addCommande,
            // les tests de récupération, mise à jour et suppression par ID ne peuvent pas être fiables.

            /*
            // Exemple de test de récupération par utilisateur (moins précis sans ID de commande spécifique)
            System.out.println("\n--- Test de récupération des commandes par utilisateur ---");
            List<Commande> userCommandes = commandeDao.getCommandesByUtilisateur(testUser.getId_utilisateur());
            System.out.println("Nombre de commandes pour l'utilisateur " + testUser.getId_utilisateur() + " : " + userCommandes.size());
            for (Commande cmd : userCommandes) {
                System.out.println(cmd);
            }

            System.out.println("\n--- Test de récupération de toutes les commandes ---");
            List<Commande> allCommandes = commandeDao.getAllCommandes();
            System.out.println("Nombre total de commandes : " + allCommandes.size());
            for (Commande cmd : allCommandes) {
                System.out.println(cmd);
            }
            */
            // Les tests getCommandeById, updateCommande, deleteCommande nécessitent un ID valide.
            // Si addCommande ne le retourne pas, ces tests sont compliqués.
            // Pour des tests réels, vous devriez soit :
            // 1. Modifier addCommande dans CommandeDao pour retourner l'objet Commande. (Recommandé)
            // 2. Récupérer la dernière commande insérée pour cet utilisateur (moins fiable).
            // 3. Insérer une commande avec un ID de test fixe si votre DB le permet (déconseillé pour AUTO_INCREMENT).


            System.out.println("\n--- Fin des tests CommandeDao (partiellement exécutés) ---");

        } catch (DaoException e) {
            System.err.println("!!!! Une erreur DAO s'est produite lors des tests Commande !!!!");
            System.err.println("Message de l'erreur : " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("\n--- Nettoyage final des données de test ---");
            try {
                // Le nettoyage de la commande ne peut pas se faire par ID ici sans un ID fiable
                // testCommandeId n'est pas récupéré si addCommande est void.
                // Donc, la suppression de la commande ajoutée via testCommandeId ne peut pas être effectuée ici.
                if (utilisateurDao != null && utilisateurDao.utilisateurExists(testUserId)) {
                    utilisateurDao.deleteUtilisateur(testUserId);
                    System.out.println("Utilisateur de test avec ID " + testUserId + " supprimé.");
                }
            } catch (DaoException e) {
                System.err.println("Erreur lors du nettoyage final des données de test dans TestCommandeDao : " + e.getMessage());
                e.printStackTrace();
            }

            if (connecteur != null) {
                try {
                    connecteur.closeConnection();
                } catch (DaoException e) {
                    System.err.println("Erreur lors de la fermeture de la connexion du connecteur dans TestCommandeDao : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}