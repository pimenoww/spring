package com.example.labstr.models;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "bonus_card")
public class BonusCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number", unique = true, nullable = false)
    private String cardNumber;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "balance", nullable = false)
    private double balance;

    @OneToMany(mappedBy = "bonusCard", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BonusTransaction> transactions;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public BonusCard() {}

    public BonusCard(String cardNumber, String ownerName, double balance, User user) {
        this.cardNumber = cardNumber;
        this.ownerName = ownerName;
        this.balance = balance;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<BonusTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<BonusTransaction> transactions) {
        this.transactions = transactions;
    }

    public void setId(Long id) {
            this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}