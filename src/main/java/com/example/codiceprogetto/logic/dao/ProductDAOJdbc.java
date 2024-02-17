package com.example.codiceprogetto.logic.dao;

import com.example.codiceprogetto.logic.entities.Product;
import com.example.codiceprogetto.logic.exception.DAOException;
import com.example.codiceprogetto.logic.utils.DBConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductDAOJdbc implements ProductDAO {
    protected Product newProduct(ResultSet rs) throws SQLException {
        Product prod;
        prod = new Product(rs.getString("name"), rs.getString("ID"), rs.getDouble("price"), rs.getString("prodImage"));

        return prod;
    }

    protected List<Product> newProductList(ResultSet rs) throws SQLException {
        List<Product> productList = new ArrayList<>();
        Product prod;

         do {
            prod = newProduct(rs);

            productList.add(prod);
         } while(rs.next());

        return productList;
    }

    public Product fetchProduct(String prodID) {
        Connection conn = DBConnectionFactory.getConn();
        ResultSet rs = null;
        Product product = null;
        PreparedStatement stmt = null;
        String sql = "SELECT * FROM Product WHERE " + "ID" + " = ?";

        try {
            stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, prodID);

            rs = stmt.executeQuery();

            if (!rs.first()) {
                throw new DAOException("Product stock empty!");
            }

            rs.first();

            product = newProduct(rs);
        } catch (SQLException | DAOException e) {
            Logger.getAnonymousLogger().log(Level.INFO, e.getMessage());
        } finally {
            if(stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    Logger.getAnonymousLogger().log(Level.INFO, "stmt close error");
                }
            }
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    Logger.getAnonymousLogger().log(Level.INFO, "rs close error");
                }
            }
        }

        return product;
    }

    public List<Product> fetchAllProduct() {
        Connection conn = DBConnectionFactory.getConn();
        ResultSet rs = null;
        List<Product> productList = new ArrayList<>();
        PreparedStatement stmt = null;
        
        try {
            String sql = "SELECT * FROM Product";
            stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            rs = stmt.executeQuery();

            if(!rs.first()) {
                throw new DAOException("Product stock empty!");
            }

            rs.first();

            productList = newProductList(rs);
        } catch (SQLException | DAOException e) {
            Logger.getAnonymousLogger().log(Level.INFO, e.getMessage());
        } finally {
            if(stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    Logger.getAnonymousLogger().log(Level.INFO, "stmt close error");
                }
            }
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    Logger.getAnonymousLogger().log(Level.INFO, "rs close error");
                }
            }
        }

        return productList;
    }
}
