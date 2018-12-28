package com.debit_credit_card.creditcardmanager.DATABASE;

import io.realm.RealmObject;

public class CARD extends RealmObject {
    String name;
    String cvv;
    String expiry;
    String cardNumber;

    public CARD(String name, String cvv, String expiry, String cardNumber) {
        this.name = name;
        this.cvv = cvv;
        this.expiry = expiry;
        this.cardNumber = cardNumber;
    }

    public CARD() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public String toString() {
        return "CARD{" +
                "name='" + name + '\'' +
                ", cvv='" + cvv + '\'' +
                ", expiry='" + expiry + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                '}';
    }
}
