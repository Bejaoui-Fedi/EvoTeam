package tn.esprit.entities;

public class User {
    private int id;
    private String nom;
    private String email;
    private String password;
    private String role;
    private String telephone;
    private boolean actif;

    /** 1) Constructeur par défaut */
    public User() {
        this.actif = true;
    }

    /** 2) Constructeur sans id (tous les attributs sauf l'identifiant) */
    public User(String nom, String email, String password, String role, String telephone, boolean actif) {
        this.nom = nom;
        this.email = email;
        this.password = password;
        this.role = role;
        this.telephone = telephone;
        this.actif = actif;
    }

    /** Surcharge utile si tu veux garder 'actif' à true par défaut */
    public User(String nom, String email, String password, String role, String telephone) {
        this(nom, email, password, role, telephone, true);
    }

    /** 3) Constructeur avec tous les attributs (y compris id) */
    public User(int id, String nom, String email, String password, String role, String telephone, boolean actif) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.password = password;
        this.role = role;
        this.telephone = telephone;
        this.actif = actif;
    }

    // Getters / Setters
    public int getId() {
        return id;
    }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    @Override
    public String toString() {
        return "User{id=" + id + ", nom='" + nom + "', email='" + email + "', role='" + role + "'}";
    }
}