// src/com/bd/dao/CommentaireDao.java
package com.bd.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bd.entity.Commentaire;
import com.bd.exceptions.DaoException;

public class CommentaireDao {

    private Connection cn;
    private String sql;
    private PreparedStatement ps;
    private ResultSet rs;

    public CommentaireDao(Connection connection) {
        this.cn = connection;
    }

    private Commentaire mapResultSetToCommentaire(ResultSet rs) throws SQLException {
        Commentaire commentaire = new Commentaire();
        commentaire.setId_commentaire(rs.getInt("id_commentaire"));
        commentaire.setId_utilisateur(rs.getInt("id_utilisateur"));
        commentaire.setId_livre(rs.getInt("id_livre"));
        commentaire.setText_commentaire(rs.getString("text_commentaire"));
        commentaire.setNote(rs.getInt("note"));

        Timestamp sqlTimestamp = rs.getTimestamp("date_commentaire");
        if (sqlTimestamp != null) {
            commentaire.setDate_commentaire(sqlTimestamp.toLocalDateTime());
        }
        return commentaire;
    }

    private void closeResources() throws DaoException {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la fermeture des ressources JDBC : " + e.getMessage(), e);
        }
    }

    // --- Méthodes CRUD existantes et nouvelles méthodes ---

    public Commentaire addCommentaire(Commentaire commentaire) throws DaoException {
        sql = "INSERT INTO commentaires (id_utilisateur, id_livre, text_commentaire, note, date_commentaire) VALUES (?, ?, ?, ?, ?)";
        try {
            ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, commentaire.getId_utilisateur());
            ps.setInt(2, commentaire.getId_livre());
            ps.setString(3, commentaire.getText_commentaire());
            ps.setInt(4, commentaire.getNote());
            ps.setTimestamp(5, Timestamp.valueOf(commentaire.getDate_commentaire())); // Utilise la date du Commentaire

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new DaoException("La création du commentaire a échoué, aucune ligne affectée.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    commentaire.setId_commentaire(generatedKeys.getInt(1));
                } else {
                    throw new DaoException("La création du commentaire a échoué, aucun ID généré.");
                }
            }
            return commentaire;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de l'ajout du commentaire: " + e.getMessage(), e);
        } finally {
            closeResources();
        }
    }

    public Commentaire getCommentaireById(int id) throws DaoException {
        sql = "SELECT * FROM commentaires WHERE id_commentaire = ?";
        try {
            ps = cn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToCommentaire(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la récupération du commentaire avec ID: " + id, e);
        } finally {
            closeResources();
        }
    }

    public boolean updateCommentaire(Commentaire commentaire) throws DaoException {
        sql = "UPDATE commentaires SET id_utilisateur = ?, id_livre = ?, text_commentaire = ?, note = ?, date_commentaire = ? WHERE id_commentaire = ?";
        try {
            ps = cn.prepareStatement(sql);
            ps.setInt(1, commentaire.getId_utilisateur());
            ps.setInt(2, commentaire.getId_livre());
            ps.setString(3, commentaire.getText_commentaire());
            ps.setInt(4, commentaire.getNote());
            ps.setTimestamp(5, Timestamp.valueOf(commentaire.getDate_commentaire()));
            ps.setInt(6, commentaire.getId_commentaire());
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la mise à jour du commentaire: " + e.getMessage(), e);
        } finally {
            closeResources();
        }
    }

    public boolean deleteCommentaire(int id) throws DaoException {
        sql = "DELETE FROM commentaires WHERE id_commentaire = ?";
        try {
            ps = cn.prepareStatement(sql);
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la suppression du commentaire: " + e.getMessage(), e);
        } finally {
            closeResources();
        }
    }

    public List<Commentaire> getAllCommentaires() throws DaoException {
        List<Commentaire> commentaires = new ArrayList<>();
        sql = "SELECT * FROM commentaires";
        try {
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                commentaires.add(mapResultSetToCommentaire(rs));
            }
            return commentaires;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la récupération de tous les commentaires.", e);
        } finally {
            closeResources();
        }
    }

    public boolean commentaireExists(int id) throws DaoException {
        sql = "SELECT COUNT(*) FROM commentaires WHERE id_commentaire = ?";
        try {
            ps = cn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la vérification de l'existence du commentaire avec ID: " + id, e);
        } finally {
            closeResources();
        }
    }

    /**
     * Récupère tous les commentaires associés à un livre spécifique.
     * @param idLivre L'ID du livre pour lequel récupérer les commentaires.
     * @return Une liste de commentaires pour le livre spécifié.
     * @throws DaoException en cas d'erreur de base de données.
     */
    public List<Commentaire> getCommentairesByLivreId(int idLivre) throws DaoException {
        List<Commentaire> commentaires = new ArrayList<>();
        sql = "SELECT * FROM commentaires WHERE id_livre = ? ORDER BY date_commentaire DESC";
        try {
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idLivre);
            rs = ps.executeQuery();
            while (rs.next()) {
                commentaires.add(mapResultSetToCommentaire(rs));
            }
            return commentaires;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la récupération des commentaires pour le livre ID: " + idLivre, e);
        } finally {
            closeResources();
        }
    }
}