package com.moneymanager.model;

import java.math.*;
import java.time.LocalDate;

public class Expense {
    private int id;
    private BigDecimal amount;
    private int categoryId;
    private LocalDate expenseDate;
    private String description;

    public Expense(int id,BigDecimal amount,int categoryId,LocalDate expensDate,String description){
        this.id=id;
        this.amount=amount;
        this.categoryId=categoryId;
        this.expenseDate=expensDate;
        this.description=description;
    }

    public int getId(){
        return id;
    }
    public BigDecimal getAmount(){
        return amount;
    }
    public int getCategoryId(){
        return categoryId;
    }
    public LocalDate getExpenseDate(){
        return expenseDate;
    }
    public String getDescription(){
        return description;
    }

    
}
