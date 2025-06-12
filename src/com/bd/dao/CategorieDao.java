// src/com/bd/dao/CategorieDao.java
package com.bd.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.bd.entity.Categorie;
import com.bd.exceptions.DaoException;

public class CategorieDao {

    private Connection cn;
    private String sql;
    private PreparedStatement ps;
    private ResultSet rs;

    // Le constructeur prend la connexion en paramètre
    public CategorieDao(Connection connection) {
        this.cn = connection;
    }

    private Categorie mapResultSetToCategorie(ResultSet rs) throws SQLException {
        Categorie categorie = new Categorie();
        categorie.setId_categorie(rs.getInt("id_categorie"));
        categorie.setNom_categorie(rs.getString("nom_categorie"));
        return categorie;
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

    public Categorie addCategorie(Categorie categorie) throws DaoException {
        sql = "INSERT INTO categories (nom_categorie) VALUES (?)";
        try {
            ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, categorie.getNom_categorie());
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DaoException("La création de la catégorie a échoué, aucune ligne affectée.");
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    categorie.setId_categorie(generatedKeys.getInt(1));
                } else {
                    throw new DaoException("La création de la catégorie a échoué, aucun ID généré.");
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de l'ajout de la catégorie: " + e.getMessage(), e);
        } finally {
            closeResources();
        }
        return categorie;
    }

    public Categorie getCategorieById(int id) throws DaoException {
        sql = "SELECT * FROM categories WHERE id_categorie = ?";
        Categorie categorie = null;
        try {
            ps = cn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                categorie = mapResultSetToCategorie(rs);
            }
            return categorie;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la récupération de la catégorie par ID: " + e.getMessage(), e);
        } finally {
            closeResources();
        }
    }

    public Categorie getCategorieByNom(String nom) throws DaoException {
        sql = "SELECT * FROM categories WHERE nom_categorie = ?";
        Categorie categorie = null;
        try {
            ps = cn.prepareStatement(sql);
            ps.setString(1, nom);
            rs = ps.executeQuery();
            if (rs.next()) {
                categorie = mapResultSetToCategorie(rs);
            }
            return categorie;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la récupération de la catégorie par nom: " + e.getMessage(), e);
        } finally {
            closeResources();
        }
    }

    public List<Categorie> getAllCategories() throws DaoException {
        List<Categorie> categories = new ArrayList<>();
        sql = "SELECT * FROM categories";
        try {
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                categories.add(mapResultSetToCategorie(rs));
            }
            return categories;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la récupération de toutes les catégories: " + e.getMessage(), e);
        } finally {
            closeResources();
        }
    }

    public boolean updateCategorie(Categorie categorie) throws DaoException {
        sql = "UPDATE categories SET nom_categorie = ? WHERE id_categorie = ?";
        try {
            ps = cn.prepareStatement(sql);
            ps.setString(1, categorie.getNom_categorie());
            ps.setInt(2, categorie.getId_categorie());
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la mise à jour de la catégorie: " + e.getMessage(), e);
        } finally {
            closeResources();
        }
    }

    public boolean deleteCategorie(int id) throws DaoException {
        sql = "DELETE FROM categories WHERE id_categorie = ?";
        try {
            ps = cn.prepareStatement(sql);
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la suppression de la catégorie: " + e.getMessage(), e);
        } finally {
            closeResources();
        }
    }

    public boolean categorieExists(int id) throws DaoException {
        sql = "SELECT COUNT(*) FROM categories WHERE id_categorie = ?";
        try {
            ps = cn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la vérification de l'existence de la catégorie avec ID: " + id, e);
        } finally {
            closeResources();
        }
    }
}