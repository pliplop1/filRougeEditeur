// src/com/bd/entity/Commentaire.java

package com.bd.entity;

import java.time.LocalDateTime;

public class Commentaire {

    private int id_commentaire;
    private int id_utilisateur;
    private int id_livre;
    private String text_commentaire;
    private int note;
    private LocalDateTime date_commentaire;

    public Commentaire() {
    }

    // Constructeur complet
    public Commentaire(int id_commentaire, int id_utilisateur, int id_livre, String text_commentaire, int note, LocalDateTime date_commentaire) {
        this.id_commentaire = id_commentaire;
        this.id_utilisateur = id_utilisateur;
        this.id_livre = id_livre;
        this.text_commentaire = text_commentaire;
        this.note = note;
        this.date_commentaire = date_commentaire;
    }

    // Constructeur pour l'ajout (ID généré par la DB, date par l'application ou DB)
    public Commentaire(int id_utilisateur, int id_livre, String text_commentaire, int note) {
        this.id_utilisateur = id_utilisateur;
        this.id_livre = id_livre;
        this.text_commentaire = text_commentaire;
        this.note = note;
        this.date_commentaire = LocalDateTime.now();
    }

    // Getters et Setters
    public int getId_commentaire() { return id_commentaire; }
    public void setId_commentaire(int id_commentaire) { this.id_commentaire = id_commentaire; }
    public int getId_utilisateur() { return id_utilisateur; }
    public void setId_utilisateur(int id_utilisateur) { this.id_utilisateur = id_utilisateur; }
    public int getId_livre() { return id_livre; }
    public void setId_livre(int id_livre) { this.id_livre = id_livre; }

    public String getText_commentaire() { return text_commentaire; }
    public void setText_commentaire(String text_commentaire) { this.text_commentaire = text_commentaire; }

    public int getNote() { return note; }
    public void setNote(int note) {
        if (note < 1 || note > 5) {
            throw new IllegalArgumentException("La note doit être comprise entre 1 et 5.");
        }
        this.note = note;
    }
    public LocalDateTime getDate_commentaire() { return date_commentaire; }
    public void setDate_commentaire(LocalDateTime date_commentaire) { this.date_commentaire = date_commentaire; }

    @Override
    public String toString() {
        return "Commentaire{" +
                "id_commentaire=" + id_commentaire +
                ", id_utilisateur=" + id_utilisateur +
                ", id_livre=" + id_livre +
                ", text_commentaire='" + text_commentaire + '\'' +
                ", note=" + note +
                ", date_commentaire=" + date_commentaire +
                '}';
    }
}