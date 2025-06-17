// src/com/bd/entity/Livre.java
package com.bd.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Livre {
    private int id_livre;
    private String titre;
    private String auteur;
    private String descriptif;
    private BigDecimal prix;
    private String url_image_couverture;
    private String uri_fichier_livre;
    private LocalDate date_publication;
    private int id_categorie;

    public Livre() {
    }

    // Constructeur complet avec ID (pour lecture depuis la DB)
    public Livre(int id_livre, String titre, String auteur, String descriptif, BigDecimal prix,
                 String url_image_couverture, String uri_fichier_livre, LocalDate date_publication, int id_categorie) {
        this.id_livre = id_livre;
        this.titre = titre;
        this.auteur = auteur;
        this.descriptif = descriptif;
        this.prix = prix;
        this.url_image_couverture = url_image_couverture;
        this.uri_fichier_livre = uri_fichier_livre;
        this.date_publication = date_publication;
        this.id_categorie = id_categorie;
    }

    // Constructeur pour l'ajout (sans ID, avec tous les autres champs)
    public Livre(String titre, String auteur, String descriptif, BigDecimal prix,
                 String url_image_couverture, String uri_fichier_livre, LocalDate date_publication, int id_categorie) {
        this.titre = titre;
        this.auteur = auteur;
        this.descriptif = descriptif;
        this.prix = prix;
        this.url_image_couverture = url_image_couverture;
        this.uri_fichier_livre = uri_fichier_livre;
        this.date_publication = date_publication;
        this.id_categorie = id_categorie;
    }

    // Getters et Setters
    public int getId_livre() { return id_livre; }
    public void setId_livre(int id_livre) { this.id_livre = id_livre; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getAuteur() { return auteur; }
    public void setAuteur(String auteur) { this.auteur = auteur; }

    public String getDescriptif() { return descriptif; }
    public void setDescriptif(String descriptif) { this.descriptif = descriptif; }

    public BigDecimal getPrix() { return prix; }
    public void setPrix(BigDecimal prix) { this.prix = prix; }

    public String getUrl_image_couverture() { return url_image_couverture; }
    public void setUrl_image_couverture(String url_image_couverture) { this.url_image_couverture = url_image_couverture; }

    public String getUri_fichier_livre() { return uri_fichier_livre; }
    public void setUri_fichier_livre(String uri_fichier_livre) { this.uri_fichier_livre = uri_fichier_livre; }

    public LocalDate getDate_publication() { return date_publication; }
    public void setDate_publication(LocalDate date_publication) { this.date_publication = date_publication; }

    public int getId_categorie() { return id_categorie; }
    public void setId_categorie(int id_categorie) { this.id_categorie = id_categorie; }

    @Override
    public String toString() {
        return "Livre{" +
                "id_livre=" + id_livre +
                ", titre='" + titre + '\'' +
                ", auteur='" + auteur + '\'' +
                ", descriptif='" + descriptif + '\'' +
                ", prix=" + prix +
                ", url_image_couverture='" + url_image_couverture + '\'' +
                ", uri_fichier_livre='" + uri_fichier_livre + '\'' +
                ", date_publication=" + date_publication +
                ", id_categorie=" + id_categorie +
                '}';
    }
}