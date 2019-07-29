package com.debit_credit_card.creditcardmanager.DATABASE;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;

public class EXPENSE extends RealmObject {
    CARD cardname;
    String expensename;
    String expensemoney;
    Date expensedate;
    String expensetype;
    String datemonth;

    public CARD getCardname() {
        return cardname;
    }

    public void setCardname(CARD cardname) {
        this.cardname = cardname;
    }

    public String getExpensename() {
        return expensename;
    }

    public void setExpensename(String expensename) {
        this.expensename = expensename;
    }

    public String getExpensemoney() {
        return expensemoney;
    }

    public void setExpensemoney(String expensemoney) {
        this.expensemoney = expensemoney;
    }

    public Date getExpensedate() {
        return expensedate;
    }

    public void setExpensedate(Date expensedate) {
        this.expensedate = expensedate;
    }

    public String getExpensetype() {
        return expensetype;
    }

    public void setExpensetype(String expensetype) {
        this.expensetype = expensetype;
    }

    public String getDatemonth() {
        return datemonth;
    }

    public void setDatemonth(String datemonth) {
        this.datemonth = datemonth;
    }

    @Override
    public String toString() {
        return "EXPENSE{" +
                "cardname=" + cardname +
                ", expensename='" + expensename + '\'' +
                ", expensemoney='" + expensemoney + '\'' +
                ", expensedate=" + expensedate +
                ", expensetype='" + expensetype + '\'' +
                ", datemonth='" + datemonth + '\'' +
                '}';
    }
}
