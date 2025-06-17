package com.bd.dao;

import com.bd.entity.Commande;
import com.bd.exceptions.DaoException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class CommandeDao {

    private Connection cn;
    private String sql;
    private PreparedStatement ps;
    private ResultSet rs;

    public CommandeDao(Connection connection) {
        this.cn = connection;
    }

    // Méthode utilitaire pour mapper un ResultSet à un objet Commande
    private Commande mapResultSetToCommande(ResultSet rs) throws SQLException {
        int id_commande = rs.getInt("id_commande");
        int id_utilisateur = rs.getInt("id_utilisateur");
        LocalDateTime date_commande = rs.getTimestamp("date_commande").toLocalDateTime();
        String statut = rs.getString("statut");
        BigDecimal montant_total = rs.getBigDecimal("montant_total");

        return new Commande(id_commande, id_utilisateur, date_commande, statut, montant_total);
    }

    // Méthode utilitaire pour fermer les ressources JDBC
    private void closeResources() {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            // La connexion n'est PAS fermée ici car elle est gérée par ConnecteurMysql
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture des ressources JDBC : " + e.getMessage());
        }
    }

    // --- Méthodes CRUD ---

    public Commande addCommande(Commande commande) throws DaoException { // <-- CHANGEMENT ICI (void -> Commande)
        // date_commande et statut sont gérés par la DB
        sql = "INSERT INTO commandes (id_utilisateur, montant_total) VALUES (?, ?)";
        try {
            ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, commande.getId_utilisateur());
            ps.setBigDecimal(2, commande.getMontant_total());
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DaoException("La création de la commande a échoué, aucune ligne affectée.");
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    commande.setId_commande(generatedKeys.getInt(1));
                    // Récupérer la date_commande et le statut par défaut si nécessaire
                    Commande insertedCommande = getCommandeById(commande.getId_commande());
                    if(insertedCommande != null) {
                        commande.setDate_commande(insertedCommande.getDate_commande());
                        commande.setStatut(insertedCommande.getStatut());
                    }
                } else {
                    throw new DaoException("La création de la commande a échoué, aucun ID généré.");
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de l'ajout de la commande: " + e.getMessage(), e);
        } finally {
            closeResources();
        }
        return commande; // <-- CHANGEMENT ICI (ajout du return)
    }

    public Commande getCommandeById(int id) throws DaoException { // Renommé de findCommandeById
        sql = "SELECT * FROM commandes WHERE id_commande = ?";
        Commande commande = null;
        try {
            ps = cn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                commande = mapResultSetToCommande(rs);
            }
            return commande;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la récupération de la commande par ID: " + e.getMessage(), e);
        } finally {
            closeResources();
        }
    }

    public List<Commande> getAllCommandes() throws DaoException {
        List<Commande> commandes = new ArrayList<>();
        sql = "SELECT * FROM commandes";
        try {
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                commandes.add(mapResultSetToCommande(rs));
            }
            return commandes;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la récupération de toutes les commandes: " + e.getMessage(), e);
        } finally {
            closeResources();
        }
    }

    public boolean updateCommande(Commande commande) throws DaoException {
        // La date_commande n'est généralement pas mise à jour manuellement
        sql = "UPDATE commandes SET id_utilisateur = ?, statut = ?, montant_total = ? WHERE id_commande = ?";
        try {
            ps = cn.prepareStatement(sql);
            ps.setInt(1, commande.getId_utilisateur());
            ps.setString(2, commande.getStatut());
            ps.setBigDecimal(3, commande.getMontant_total());
            ps.setInt(4, commande.getId_commande());
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la mise à jour de la commande: " + e.getMessage(), e);
        } finally {
            closeResources();
        }
    }

    public boolean deleteCommande(int id) throws DaoException {
        sql = "DELETE FROM commandes WHERE id_commande = ?";
        try {
            ps = cn.prepareStatement(sql);
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la suppression de la commande: " + e.getMessage(), e);
        } finally {
            closeResources();
        }
    }

    public boolean commandeExists(int id) throws DaoException {
        sql = "SELECT COUNT(*) FROM commandes WHERE id_commande = ?";
        try {
            ps = cn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la vérification de l'existence de la commande avec ID: " + id, e);
        } finally {
            closeResources();
        }
    }

    /**
     * Récupère toutes les commandes associées à un utilisateur spécifique.
     * @param idUtilisateur L'ID de l'utilisateur.
     * @return Une liste de commandes passées par cet utilisateur.
     * @throws DaoException en cas d'erreur de base de données.
     */
    public List<Commande> getCommandesByUtilisateur(int idUtilisateur) throws DaoException {
        List<Commande> commandes = new ArrayList<>();
        sql = "SELECT * FROM commandes WHERE id_utilisateur = ?";
        try {
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idUtilisateur);
            rs = ps.executeQuery();
            while (rs.next()) {
                commandes.add(mapResultSetToCommande(rs));
            }
            return commandes;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la récupération des commandes pour l'utilisateur ID: " + idUtilisateur, e);
        } finally {
            closeResources();
        }
    }
}