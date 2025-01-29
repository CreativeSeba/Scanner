package org.example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Products {
    ArrayList<Product> productsList = new ArrayList<>();

    public Products() {
        getProducts();
    }

    public void getProducts() {
        String query = "SELECT * FROM Products";

        try (Connection connection = DbConnection.connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                long barcode = resultSet.getLong("barcode");
                int stock = resultSet.getInt("stock");

                Product product = new Product(name, price, barcode, stock);
                productsList.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Product searchProduct(long barcode) {
        for (Product product : productsList) {
            if (product.barcode == barcode) {
                return product;
            }
        }
        return null;
    }
}
