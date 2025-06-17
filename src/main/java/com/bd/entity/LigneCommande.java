package com.bd.entity;

import java.math.BigDecimal;

public class LigneCommande {

    private int id_ligne_commande;
    private int id_commande;
    private int id_livre;
    private int quantite;
    private BigDecimal prix_unitaire;

    public LigneCommande() {
    }

    // Constructeur pour l'ajout (ID géré par la DB)
    public LigneCommande(int id_commande, int id_livre, int quantite, BigDecimal prix_unitaire) {
        this.id_commande = id_commande;
        this.id_livre = id_livre;
        this.quantite = quantite;
        this.prix_unitaire = prix_unitaire;
    }

    // Constructeur complet (utile pour récupérer depuis la DB)
    public LigneCommande(int id_ligne_commande, int id_commande, int id_livre, int quantite, BigDecimal prix_unitaire) {
        this.id_ligne_commande = id_ligne_commande;
        this.id_commande = id_commande;
        this.id_livre = id_livre;
        this.quantite = quantite;
        this.prix_unitaire = prix_unitaire;
    }

    // --- Getters et Setters ---
    public int getId_ligne_commande() { return id_ligne_commande; }
    public void setId_ligne_commande(int id_ligne_commande) { this.id_ligne_commande = id_ligne_commande; }
    public int getId_commande() { return id_commande; }
    public void setId_commande(int id_commande) { this.id_commande = id_commande; }
    public int getId_livre() { return id_livre; }
    public void setId_livre(int id_livre) { this.id_livre = id_livre; }
    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
    public BigDecimal getPrix_unitaire() { return prix_unitaire; }
    public void setPrix_unitaire(BigDecimal prix_unitaire) { this.prix_unitaire = prix_unitaire; }

    @Override
    public String toString() {
        return "LigneCommande{" +
                "id_ligne_commande=" + id_ligne_commande +
                ", id_commande=" + id_commande +
                ", id_livre=" + id_livre +
                ", quantite=" + quantite +
                ", prix_unitaire=" + prix_unitaire +
                '}';
    }
}