package com.bd.dao;

import com.bd.entity.LigneCommande;
import com.bd.exceptions.DaoException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class LigneCommandeDao {

    private Connection cn;
    private String sql;
    private PreparedStatement ps;
    private ResultSet rs;

    public LigneCommandeDao(Connection connection) {
        this.cn = connection;
    }

    // Méthode utilitaire pour mapper un ResultSet à un objet LigneCommande
    private LigneCommande mapResultSetToLigneCommande(ResultSet rs) throws SQLException {
        int id_ligne_commande = rs.getInt("id_ligne_commande");
        int id_commande = rs.getInt("id_commande");
        int id_livre = rs.getInt("id_livre");
        int quantite = rs.getInt("quantite");
        BigDecimal prix_unitaire = rs.getBigDecimal("prix_unitaire");

        return new LigneCommande(id_ligne_commande, id_commande, id_livre, quantite, prix_unitaire);
    }

    // Méthode utilitaire pour fermer les ressources JDBC
    private void closeResources() {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture des ressources JDBC : " + e.getMessage());
        }
    }

    // --- Méthodes CRUD ---

    public void addLigneCommande(LigneCommande ligneCommande) throws DaoException {
        sql = "INSERT INTO lignes_commande (id_commande, id_livre, quantite, prix_unitaire) VALUES (?, ?, ?, ?)";
        try {
            ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, ligneCommande.getId_commande());
            ps.setInt(2, ligneCommande.getId_livre());
            ps.setInt(3, ligneCommande.getQuantite());
            ps.setBigDecimal(4, ligneCommande.getPrix_unitaire());
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DaoException("La création de la ligne de commande a échoué, aucune ligne affectée.");
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ligneCommande.setId_ligne_commande(generatedKeys.getInt(1));
                } else {
                    throw new DaoException("La création de la ligne de commande a échoué, aucun ID généré.");
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de l'ajout de la ligne de commande: " + e.getMessage(), e);
        } finally {
            closeResources();
        }
    }

    public List<LigneCommande> getLignesCommandeByCommandeId(int idCommande) throws DaoException {
        List<LigneCommande> lignesCommande = new ArrayList<>();
        sql = "SELECT * FROM lignes_commande WHERE id_commande = ?";
        try {
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idCommande);
            rs = ps.executeQuery();
            while (rs.next()) {
                lignesCommande.add(mapResultSetToLigneCommande(rs));
            }
            return lignesCommande;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la récupération des lignes de commande pour la commande ID: " + idCommande, e);
        } finally {
            closeResources();
        }
    }

    // Vous pouvez ajouter d'autres méthodes CRUD (update, delete, getById) si nécessaire
}