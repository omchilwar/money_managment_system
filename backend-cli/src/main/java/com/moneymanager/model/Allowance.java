package com.moneymanager.model;

import java.math.*;
import java.time.*;
public class Allowance {
    private int id;
    private BigDecimal amount;
    private String monthYear;
    private LocalDate receivedDate;

    public Allowance(int id, BigDecimal amount,
                     String monthYear, LocalDate receivedDate){
                         this.id = id;
                         this.amount = amount;
                         this.monthYear = monthYear;
                         this.receivedDate = receivedDate;
                     }
                       public int getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }
}
