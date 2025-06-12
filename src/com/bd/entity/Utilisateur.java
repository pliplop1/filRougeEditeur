// src/com/bd/entity/Utilisateur.java
package com.bd.entity;

import java.time.LocalDate;

public class Utilisateur {
    private int id_utilisateur;
    private String nom;
    private String prenom;
    private String email;
    private String mot_de_passe_hash;
    private String adresse;
    private String ville;
    private String code_postal;
    private String pays;
    private LocalDate date_inscription;
    private String role; // Ex: "client", "admin"

    public Utilisateur() {
    }

    // Constructeur complet avec ID (pour lecture depuis la DB)
    public Utilisateur(int id_utilisateur, String nom, String prenom, String email, String mot_de_passe_hash,
                       String adresse, String ville, String code_postal, String pays, LocalDate date_inscription, String role) {
        this.id_utilisateur = id_utilisateur;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mot_de_passe_hash = mot_de_passe_hash;
        this.adresse = adresse;
        this.ville = ville;
        this.code_postal = code_postal;
        this.pays = pays;
        this.date_inscription = date_inscription;
        this.role = role;
    }

    // CORRECTION : Constructeur pour l'ajout (sans ID, avec tous les autres champs, incluant date_inscription)
    public Utilisateur(String nom, String prenom, String email, String mot_de_passe_hash,
                       String adresse, String ville, String code_postal, String pays, LocalDate date_inscription, String role) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mot_de_passe_hash = mot_de_passe_hash;
        this.adresse = adresse;
        this.ville = ville;
        this.code_postal = code_postal;
        this.pays = pays;
        this.date_inscription = date_inscription;
        this.role = role;
    }

    // Getters et Setters
    public int getId_utilisateur() { return id_utilisateur; }
    public void setId_utilisateur(int id_utilisateur) { this.id_utilisateur = id_utilisateur; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMot_de_passe_hash() { return mot_de_passe_hash; }
    public void setMot_de_passe_hash(String mot_de_passe_hash) { this.mot_de_passe_hash = mot_de_passe_hash; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getCode_postal() { return code_postal; }
    public void setCode_postal(String code_postal) { this.code_postal = code_postal; }

    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }

    public LocalDate getDate_inscription() { return date_inscription; }
    public void setDate_inscription(LocalDate date_inscription) { this.date_inscription = date_inscription; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "id_utilisateur=" + id_utilisateur +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", mot_de_passe_hash='" + mot_de_passe_hash + '\'' +
                ", adresse='" + adresse + '\'' +
                ", ville='" + ville + '\'' +
                ", code_postal='" + code_postal + '\'' +
                ", pays='" + pays + '\'' +
                ", date_inscription=" + date_inscription +
                ", role='" + role + '\'' +
                '}';
    }
}