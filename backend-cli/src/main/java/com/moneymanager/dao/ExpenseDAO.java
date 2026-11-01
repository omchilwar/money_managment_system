package com.moneymanager.dao;
import com.moneymanager.*;
import com.moneymanager.model.Expense;
import com.mysql.cj.protocol.Resultset;

import java.util.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;

public class ExpenseDAO {


    // this sends the values we have given to the sql databse (INSERT)
    
    public boolean addExpense(Expense expense){
        String sql="INSERT INTO expenses"+"(amount, category_id, expense_date, description)"+"VALUES (?,?,?,?)";


        try(
            Connection conn=DBconnection.getConnection();
            PreparedStatement pstmt= conn.prepareStatement(sql);

        ){

            pstmt.setBigDecimal(1, expense.getAmount());
            pstmt.setInt(2, expense.getCategoryId());
            pstmt.setDate(3, java.sql.Date.valueOf(expense.getExpenseDate()));
            pstmt.setString(4, expense.getDescription());
            int rows= pstmt.executeUpdate();

            return rows > 0;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }


    // this takes the values and we get all the values of the expenses(SELECT)

    public List<Expense> getAllExpenses(){
        List<Expense> expenses =new ArrayList<>();
        String sql ="SELECT * FROM expenses";

        try(
            Connection conn=DBconnection.getConnection();
            PreparedStatement pstmt=conn.prepareStatement(sql);
            ResultSet rs= pstmt.executeQuery();
        ){
            while (rs.next()) {
                int id=rs.getInt("id");
                BigDecimal amount=rs.getBigDecimal("amount");
                int categoryId=rs.getInt("category_id");

                LocalDate expensDate=rs.getDate("expense_date").toLocalDate();

                String description=rs.getString("description");

                Expense expense=new Expense(id, amount, categoryId, expensDate, description);

                expenses.add(expense);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return expenses;
    }


    // removing the rows from the table (DELETE)

    public boolean deleteExpense(int id){

        String sql = "DELETE FROM expenses WHERE id = ?";
        

        try(
            Connection conn=DBconnection.getConnection();
            PreparedStatement pstmt =conn.prepareStatement(sql);

        ){
            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();

            return rows>0;

        }catch(SQLException e){
        e.printStackTrace();

        return false;

    }

    
    }



}
