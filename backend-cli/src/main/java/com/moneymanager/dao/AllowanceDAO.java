package com.moneymanager.dao;

import com.moneymanager.model.Allowance;
import com.moneymanager.DBconnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class AllowanceDAO {
    public boolean addAllowance(Allowance allowance){

        String sql = "INSERT INTO allowance (amount, month_year, received_date) VALUES (?, ?, ?)";

        try(
            Connection conn =DBconnection.getConnection();
            PreparedStatement pstmt= conn.prepareStatement(sql);
        ){
            pstmt.setBigDecimal(1, allowance.getAmount());
            pstmt.setString(2, allowance.getMonthYear());
            pstmt.setDate(3, java.sql.Date.valueOf(allowance.getReceivedDate()));

            int rows = pstmt.executeUpdate();

            return rows>0;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
        //  getting all allowance

    public List<Allowance> getAllAllowances(){
            List<Allowance> allowences = new ArrayList<>();

            String sql2 ="SELECT * FROM allowance";

            try(
                Connection conn= DBconnection.getConnection();
                PreparedStatement pstmt=conn.prepareStatement(sql2);
                ResultSet rs= pstmt.executeQuery();
            ){
                while (rs.next()) {
                    
                    int id=rs.getInt("id");

                    BigDecimal amount=rs.getBigDecimal("amount");

                    String monthYear =rs.getString("month_year");

                    LocalDate receivedDate =rs.getDate("received_date").toLocalDate();

                    Allowance allowance2= new Allowance(id, amount, monthYear, receivedDate);

                    allowences.add(allowance2);
                }

                }   catch(SQLException e){
                        e.printStackTrace();

                }
            

            return allowences;

        }

        // update allowance 

        public boolean updateAllowance(Allowance allowance){

            String sql = "UPDATE allowance SET amount = ?, " +
                 "month_year = ?, received_date = ? " +
                 "WHERE id = ?";

                 try(
                    Connection conn= DBconnection.getConnection();
                    PreparedStatement pstmt=conn.prepareStatement(sql);
                 ){
                    pstmt.setBigDecimal(1, allowance.getAmount());

                    pstmt.setString(2, allowance.getMonthYear());

                    pstmt.setDate(3, java.sql.Date.valueOf(allowance.getReceivedDate()));

                    pstmt.setInt(4, allowance.getId());

                    int rows=pstmt.executeUpdate();

                    return rows>0;
                 }catch(SQLException e){
                    e.getStackTrace();
                    return false;
                 }
        }

        // deleting the allowance

        public boolean deleteAllowance(int id){

            String sql ="DELETE FROM allowance WHERE id =?";

            try(
                Connection conn = DBconnection.getConnection();
                PreparedStatement pstmt=conn.prepareStatement(sql);

            ){
                pstmt.setInt(1, id);

                int rows = pstmt.executeUpdate();
                return rows>0;
            }catch(SQLException e){
                e.getStackTrace();
                return false;
            }

        }
    
}
