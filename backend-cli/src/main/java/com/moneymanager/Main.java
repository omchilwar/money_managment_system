package com.moneymanager;

import  com.moneymanager.*;
import com.moneymanager.dao.CategoryDAO;
import com.moneymanager.dao.ExpenseDAO;
import com.moneymanager.model.Category;
import com.moneymanager.model.Expense;

import java.math.*;
import java.time.*;

import java.util.*;
public class Main {
    public static void main(String[] args) {
        DBconnection.getConnection();
        Scanner sc=new Scanner(System.in);


// this is for category section

        CategoryDAO dao= new  CategoryDAO();
        List<Category> categories= dao.getAllCategories();
        
        for(Category c: categories){
            System.out.println(c.getId()+" - "+c.getName());
        }


// this is for expense insertion section

        

        System.out.print("enter the id : ");
        int id=sc.nextInt();

        System.out.print("enter the amount : ");
        BigDecimal amount=sc.nextBigDecimal();

        System.out.print("enter the category id : ");
        int categoryId=sc.nextInt();

        System.out.print("enter the description : ");
        String description=sc.next();


        Expense expense =new Expense(id, amount, categoryId, LocalDate.now(), description);
        

        ExpenseDAO expenseDAO=new ExpenseDAO();

        boolean result =expenseDAO.addExpense(expense);

        if (result) {
            System.out.println("expense added successfully! ");
        }else{
            System.out.println("failed to add expense");
        }

// getting the expense from the sql
        List<Expense> expenses =expenseDAO.getAllExpenses();

        System.out.println("\n-------- All expenses ---------");

        for(Expense e:expenses){
            System.out.println(
                e.getId() + " | " +
                e.getAmount() + " | " +
                e.getCategoryId() + " | " +
                e.getExpenseDate() + " | " +
                e.getDescription()
            );
        }

        // deleting an expense

        System.out.print("enter the id you want to remove : ");
        int expenseId = sc.nextInt();

        boolean delete =expenseDAO.deleteExpense(expenseId);

        if (delete) {
            System.out.println("expense deleted successfully");
        }else{
            System.out.println("expense not found! ");
        }


        sc.close();
    }
}