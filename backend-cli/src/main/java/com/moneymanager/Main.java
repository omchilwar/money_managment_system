package com.moneymanager;

import  com.moneymanager.*;
import com.moneymanager.dao.CategoryDAO;
import com.moneymanager.model.Category;

import java.util.*;
public class Main {
    public static void main(String[] args) {
        DBconnection.getConnection();

        CategoryDAO dao= new  CategoryDAO();
        List<Category> categories= dao.getAllCategories();
        
        for(Category c: categories){
            System.out.println(c.getId()+" - "+c.getName());
        }
    }
}