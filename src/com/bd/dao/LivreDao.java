// src/com/bd/dao/LivreDao.java
package com.bd.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

import com.bd.entity.Livre;
import com.bd.exceptions.DaoException;

public class LivreDao {

    private Connection cn;
    private String sql;
    private PreparedStatement ps;
    private ResultSet rs;

    public LivreDao(Connection connection) {
        this.cn = connection;
    }

    private Livre mapResultSetToLivre(ResultSet rs) throws SQLException {
        Livre livre = new Livre();
        livre.setId_livre(rs.getInt("id_livre"));
        livre.setTitre(rs.getString("titre"));
        livre.setAuteur(rs.getString("auteur"));
        livre.setDescriptif(rs.getString("descriptif"));
        livre.setPrix(rs.getBigDecimal("prix"));
        livre.setUrl_image_couverture(rs.getString("url_image_couverture"));
        livre.setUri_fichier_livre(rs.getString("uri_fichier_livre"));
        livre.setDate_publication(rs.getDate("date_publication").toLocalDate());
        livre.setId_categorie(rs.getInt("id_categorie"));
        return livre;
    }

    private void closeResources() {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture des ressources JDBC : " + e.getMessage());
        }
    }

    // --- Méthodes CRUD ---

    public Livre addLivre(Livre livre) throws DaoException {
        sql = "INSERT INTO livres (titre, auteur, descriptif, prix, url_image_couverture, uri_fichier_livre, date_publication, id_categorie) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, livre.getTitre());
            ps.setString(2, livre.getAuteur());
            ps.setString(3, livre.getDescriptif());
            ps.setBigDecimal(4, livre.getPrix());
            ps.setString(5, livre.getUrl_image_couverture());
            ps.setString(6, livre.getUri_fichier_livre());
            ps.setObject(7, livre.getDate_publication());
            ps.setInt(8, livre.getId_categorie());
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DaoException("La création du livre a échoué, aucune ligne affectée.");
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    livre.setId_livre(generatedKeys.getInt(1));
                } else {
                    throw new DaoException("La création du livre a échoué, aucun ID généré.");
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de l'ajout du livre: " + e.getMessage(), e);
        } finally {
            closeResources();
        }
        return livre;
    }

    // CORRECTION : Renommé de findLivreById
    public Livre getLivreById(int id) throws DaoException {
        sql = "SELECT * FROM livres WHERE id_livre = ?";
        Livre livre = null;
        try {
            ps = cn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                livre = mapResultSetToLivre(rs);
            }
            return livre;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la récupération du livre par ID: " + e.getMessage(), e);
        } finally {
            closeResources();
        }
    }

    // NOUVELLE MÉTHODE : getLivreByTitre pour résoudre l'erreur
    public Livre getLivreByTitre(String titre) throws DaoException {
        sql = "SELECT * FROM livres WHERE titre = ?";
        Livre livre = null;
        try {
            ps = cn.prepareStatement(sql);
            ps.setString(1, titre);
            rs = ps.executeQuery();
            if (rs.next()) {
                livre = mapResultSetToLivre(rs);
            }
            return livre;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la récupération du livre par titre: " + e.getMessage(), e);
        } finally {
            closeResources();
        }
    }

    public List<Livre> getAllLivres() throws DaoException {
        List<Livre> livres = new ArrayList<>();
        sql = "SELECT * FROM livres";
        try {
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                livres.add(mapResultSetToLivre(rs));
            }
            return livres;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la récupération de tous les livres: " + e.getMessage(), e);
        } finally {
            closeResources();
        }
    }

    public boolean updateLivre(Livre livre) throws DaoException {
        sql = "UPDATE livres SET titre = ?, auteur = ?, descriptif = ?, prix = ?, url_image_couverture = ?, uri_fichier_livre = ?, date_publication = ?, id_categorie = ? WHERE id_livre = ?";
        try {
            ps = cn.prepareStatement(sql);
            ps.setString(1, livre.getTitre());
            ps.setString(2, livre.getAuteur());
            ps.setString(3, livre.getDescriptif());
            ps.setBigDecimal(4, livre.getPrix());
            ps.setString(5, livre.getUrl_image_couverture());
            ps.setString(6, livre.getUri_fichier_livre());
            ps.setObject(7, livre.getDate_publication());
            ps.setInt(8, livre.getId_categorie());
            ps.setInt(9, livre.getId_livre());
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la mise à jour du livre: " + e.getMessage(), e);
        } finally {
            closeResources();
        }
    }

    public boolean deleteLivre(int id) throws DaoException {
        sql = "DELETE FROM livres WHERE id_livre = ?";
        try {
            ps = cn.prepareStatement(sql);
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la suppression du livre: " + e.getMessage(), e);
        } finally {
            closeResources();
        }
    }

    public boolean livreExists(int id) throws DaoException {
        sql = "SELECT COUNT(*) FROM livres WHERE id_livre = ?";
        try {
            ps = cn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la vérification de l'existence du livre avec ID: " + id, e);
        } finally {
            closeResources();
        }
    }
}