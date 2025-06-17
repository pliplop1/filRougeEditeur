// src/com/bd/dao/UtilisateurDao.java
package com.bd.dao;

import com.bd.entity.Utilisateur;
import com.bd.exceptions.DaoException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDao {

    private Connection cn;
    private String sql;
    private PreparedStatement ps;
    private ResultSet rs;

    public UtilisateurDao(Connection connection) {
        this.cn = connection;
    }

    private Utilisateur mapResultSetToUtilisateur(ResultSet rs) throws SQLException {
        int id_utilisateur = rs.getInt("id_utilisateur");
        String nom = rs.getString("nom");
        String prenom = rs.getString("prenom");
        String email = rs.getString("email");
        String mot_de_passe_hash = rs.getString("mot_de_passe_hash");
        String adresse = rs.getString("adresse");
        String ville = rs.getString("ville");
        String code_postal = rs.getString("code_postal");
        String pays = rs.getString("pays");
        LocalDate date_inscription = rs.getDate("date_inscription").toLocalDate();
        String role = rs.getString("role");

        return new Utilisateur(id_utilisateur, nom, prenom, email, mot_de_passe_hash,
                               adresse, ville, code_postal, pays, date_inscription, role);
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

    public Utilisateur addUtilisateur(Utilisateur utilisateur) throws DaoException {
        sql = "INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe_hash, adresse, ville, code_postal, pays, date_inscription, role) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, utilisateur.getNom());
            ps.setString(2, utilisateur.getPrenom());
            ps.setString(3, utilisateur.getEmail());
            ps.setString(4, utilisateur.getMot_de_passe_hash());
            ps.setString(5, utilisateur.getAdresse());
            ps.setString(6, utilisateur.getVille());
            ps.setString(7, utilisateur.getCode_postal());
            ps.setString(8, utilisateur.getPays());
            ps.setDate(9, java.sql.Date.valueOf(utilisateur.getDate_inscription()));
            ps.setString(10, utilisateur.getRole());
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DaoException("La création de l'utilisateur a échoué, aucune ligne affectée.");
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    utilisateur.setId_utilisateur(generatedKeys.getInt(1));
                } else {
                    throw new DaoException("La création de l'utilisateur a échoué, aucun ID généré.");
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de l'ajout de l'utilisateur: " + e.getMessage(), e);
        } finally {
            closeResources();
        }
        return utilisateur;
    }

    // CORRECTION : Renommé de findUtilisateurById
    public Utilisateur getUtilisateurById(int id) throws DaoException {
        sql = "SELECT * FROM utilisateurs WHERE id_utilisateur = ?";
        Utilisateur utilisateur = null;
        try {
            ps = cn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                utilisateur = mapResultSetToUtilisateur(rs);
            }
            return utilisateur;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la récupération de l'utilisateur par ID: " + e.getMessage(), e);
        } finally {
            closeResources();
        }
    }

    // CORRECTION : Renommé de findUtilisateurByEmail
    public Utilisateur getUtilisateurByEmail(String email) throws DaoException {
        sql = "SELECT * FROM utilisateurs WHERE email = ?";
        Utilisateur utilisateur = null;
        try {
            ps = cn.prepareStatement(sql);
            ps.setString(1, email);
            rs = ps.executeQuery();
            if (rs.next()) {
                utilisateur = mapResultSetToUtilisateur(rs);
            }
            return utilisateur;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la récupération de l'utilisateur par email: " + e.getMessage(), e);
        } finally {
            closeResources();
        }
    }

    public List<Utilisateur> getAllUtilisateurs() throws DaoException {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        sql = "SELECT * FROM utilisateurs";
        try {
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                utilisateurs.add(mapResultSetToUtilisateur(rs));
            }
            return utilisateurs;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la récupération de tous les utilisateurs: " + e.getMessage(), e);
        } finally {
            closeResources();
        }
    }

    public boolean updateUtilisateur(Utilisateur utilisateur) throws DaoException {
        sql = "UPDATE utilisateurs SET nom = ?, prenom = ?, email = ?, mot_de_passe_hash = ?, adresse = ?, ville = ?, code_postal = ?, pays = ?, role = ? WHERE id_utilisateur = ?";
        try {
            ps = cn.prepareStatement(sql);
            ps.setString(1, utilisateur.getNom());
            ps.setString(2, utilisateur.getPrenom());
            ps.setString(3, utilisateur.getEmail());
            ps.setString(4, utilisateur.getMot_de_passe_hash());
            ps.setString(5, utilisateur.getAdresse());
            ps.setString(6, utilisateur.getVille());
            ps.setString(7, utilisateur.getCode_postal());
            ps.setString(8, utilisateur.getPays());
            ps.setString(9, utilisateur.getRole());
            ps.setInt(10, utilisateur.getId_utilisateur());
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage(), e);
        } finally {
            closeResources();
        }
    }

    public boolean deleteUtilisateur(int id) throws DaoException {
        sql = "DELETE FROM utilisateurs WHERE id_utilisateur = ?";
        try {
            ps = cn.prepareStatement(sql);
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la suppression de l'utilisateur: " + e.getMessage(), e);
        } finally {
            closeResources();
        }
    }

    public boolean utilisateurExists(int id) throws DaoException {
        sql = "SELECT COUNT(*) FROM utilisateurs WHERE id_utilisateur = ?";
        try {
            ps = cn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la vérification de l'existence de l'utilisateur avec ID: " + id, e);
        } finally {
            closeResources();
        }
    }
}