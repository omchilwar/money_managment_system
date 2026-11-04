package com.moneymanager;


import com.moneymanager.dao.AllowanceDAO;
import com.moneymanager.dao.CategoryDAO;
import com.moneymanager.dao.ExpenseDAO;
import com.moneymanager.model.Allowance;
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

        


        int id=0;

        System.out.print("enter the amount : ");
        BigDecimal amount=sc.nextBigDecimal();

        System.out.print("enter the category id : ");
        int categoryId=sc.nextInt();

        System.out.print("enter the description : ");
        String description=sc.nextLine();


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


        // updating the expense
        System.out.print("Enter Expense ID to update: ");
        int updateId = sc.nextInt();

        sc.nextLine(); // Consume leftover newline

        System.out.print("Enter new amount: ");
        BigDecimal updateAmount = sc.nextBigDecimal();

        System.out.print("Enter new category ID: ");
        int updateCategoryId = sc.nextInt();

        sc.nextLine();

        System.out.print("Enter new description: ");
        String updateDescription = sc.nextLine();

        System.out.print("Enter new expense date (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(sc.nextLine());

        Expense updatedExpense = new Expense(updateId, updateAmount, updateCategoryId, date, updateDescription);
        
        boolean updateResult = expenseDAO.updateExpense(updatedExpense);

        if (updateResult) {
            System.out.println("expense updated successfully");
        }else{
            System.out.println("expense not found or nothing changed");
        }


        // inserting allowance record

        AllowanceDAO allowanceDAO = new AllowanceDAO();
        
        System.out.print("Enter allowance amount: ");
        BigDecimal allowanceAmount = sc.nextBigDecimal();
        sc.nextLine();

        System.out.print("Enter month (YYYY-MM): ");
        String allowanceMonthYear = sc.nextLine();

        System.out.print("Enter received date (YYYY-MM-DD): ");
        LocalDate allowanceReceivedDate = LocalDate.parse(sc.nextLine());

        Allowance allowance = new Allowance(0, allowanceAmount, allowanceMonthYear, allowanceReceivedDate);

        boolean allowanceResult = allowanceDAO.addAllowance(allowance);

        if (allowanceResult) {
            System.out.println("Allowance added successfully");
        }else{
            System.out.println("Failed to add allowance");
        }


        // get all the allowences

        List<Allowance> allowances = allowanceDAO.getAllAllowances();

        System.out.println("\n------- All Alloewnces -------");

        for(Allowance a:allowances){

            System.out.println(
                a.getId() + " | "+
                a.getAmount() + " | "+
                a.getMonthYear() + " | "+
                a.getReceivedDate()
            );
        }

        // updating the allowance
        System.out.print("Enter allowance ID to update: ");
        int updateAllowanceId = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter new allowance amount: ");
        BigDecimal updateAllowanceAmount = sc.nextBigDecimal();
        sc.nextLine();

        System.out.print("Enter new month (YYYY-MM): ");
        String updateAllowanceMonthYear = sc.nextLine();

        System.out.print("Enter new received date       (YYYY-MM-DD): ");
        LocalDate updateAllowanceReceivedDate = LocalDate.parse      (sc.nextLine());

        Allowance updatedAllowance = new Allowance(updateAllowanceId, updateAllowanceAmount, updateAllowanceMonthYear, updateAllowanceReceivedDate);

        boolean AllowanceResult = allowanceDAO.updateAllowance(updatedAllowance);
        if (AllowanceResult) {
            System.out.println("Allowance updated successfully");
        }else{
            System.out.println("Allowance not found");
        }

        // deleting the allowance 
        System.out.println("enter the id you want to delete : ");
        int allowanceDelete = sc.nextInt();

        boolean allowanceDeleteResult = allowanceDAO.deleteAllowance(allowanceDelete);

        if (allowanceDeleteResult) {
            System.out.println("allowance deleted successfully");
        }else{
            System.out.println("allowance not found");
        }

        sc.close();
    }
}