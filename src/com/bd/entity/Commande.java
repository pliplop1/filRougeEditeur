package com.bd.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList; // Ajouté pour initialiser la liste dans le constructeur

public class Commande {

    private int id_commande;
    private int id_utilisateur;
    private LocalDateTime date_commande;
    private String statut;
    private BigDecimal montant_total;

    private List<LigneCommande> lignesCommande;

    public Commande() {
        this.lignesCommande = new ArrayList<>(); // Initialiser la liste
    }

    // Constructeur pour ajouter une nouvelle commande (sans ID, sans date)
    public Commande(int id_utilisateur, BigDecimal montant_total) {
        this(); // Appelle le constructeur par défaut pour initialiser lignesCommande
        this.id_utilisateur = id_utilisateur;
        this.montant_total = montant_total;
        // La date et le statut par défaut sont gérés par la DB
    }

    // Constructeur complet (utile pour récupérer depuis la DB)
    public Commande(int id_commande, int id_utilisateur, LocalDateTime date_commande, String statut, BigDecimal montant_total) {
        this(); // Appelle le constructeur par défaut pour initialiser lignesCommande
        this.id_commande = id_commande;
        this.id_utilisateur = id_utilisateur;
        this.date_commande = date_commande;
        this.statut = statut;
        this.montant_total = montant_total;
    }

    // --- Getters et Setters ---

    public int getId_commande() { return id_commande; }
    public void setId_commande(int id_commande) { this.id_commande = id_commande; }
    public int getId_utilisateur() { return id_utilisateur; }
    public void setId_utilisateur(int id_utilisateur) { this.id_utilisateur = id_utilisateur; }
    public LocalDateTime getDate_commande() { return date_commande; }
    public void setDate_commande(LocalDateTime date_commande) { this.date_commande = date_commande; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public BigDecimal getMontant_total() { return montant_total; }
    public void setMontant_total(BigDecimal montant_total) { this.montant_total = montant_total; }
    public List<LigneCommande> getLignesCommande() { return lignesCommande; }
    public void setLignesCommande(List<LigneCommande> lignesCommande) { this.lignesCommande = lignesCommande; }

    @Override
    public String toString() {
        return "Commande{" +
                "id_commande=" + id_commande +
                ", id_utilisateur=" + id_utilisateur +
                ", date_commande=" + date_commande +
                ", statut='" + statut + '\'' +
                ", montant_total=" + montant_total +
                '}';
    }
}