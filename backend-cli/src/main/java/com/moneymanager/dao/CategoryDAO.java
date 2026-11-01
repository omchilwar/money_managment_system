package com.moneymanager.dao;

import com.moneymanager.DBconnection;
import com.moneymanager.model.Category;

import java.sql.*;
import java.util.*;
public class CategoryDAO {
    public List<Category> getAllCategories(){
        List<Category> categories =new ArrayList<>();

        String sql= "SELECT * FROM categories";
        try(
            Connection conn = DBconnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()
        ){
            while (rs.next()) {
                
                int id = rs.getInt("id");
                String name= rs.getString("name");

                Category category = new Category(id, name);

                categories.add(category);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return categories;
    }
}
