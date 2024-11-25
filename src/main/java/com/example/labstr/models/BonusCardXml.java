package com.example.labstr.models;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "bonusCard")
public class BonusCardXml {

    private String cardNumber;
    private String ownerName;
    private double balance;

    public BonusCardXml() {}

    public BonusCardXml(String cardNumber, String ownerName, double balance) {
        this.cardNumber = cardNumber;
        this.ownerName = ownerName;
        this.balance = balance;
    }

    @XmlElement
    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @XmlElement
    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @XmlElement
    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}