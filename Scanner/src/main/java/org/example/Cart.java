package org.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;

public class Cart {
    float total = 0;
    HashMap<Product, Integer> products;


    public Cart() {
        products = new HashMap<>();
    }

    public void addProduct(Product productTemp) {
        if (!products.containsKey(productTemp)) {
            products.put(productTemp, 1);
        }
        else{
            products.put(productTemp, products.get(productTemp) + 1);

        }
    }
    public void printProducts() {
        int nameWidth = 20;
        int priceWidth = 10;
        total = 0;

        System.out.printf("%-" + nameWidth + "s %10s %10s%n", "Product", "Quantity", "Price");
        System.out.println("=".repeat(nameWidth + priceWidth + 15));

        for (Product product : products.keySet()) {
            float multi = (float) (product.price * products.get(product));
            String formattedPrice = formatNumber(multi);
            System.out.printf("%-" + nameWidth + "s %10s %10s zł (%d * %.2f zł)%n",
                    product.name,
                    products.get(product),
                    formattedPrice,
                    products.get(product),
                    product.price);
            total += multi;
        }
        System.out.println("=".repeat(nameWidth + priceWidth + 15));
        System.out.printf("%-" + nameWidth + "s %10s %10s zł%n", "Total", "", formatNumber(total));
    }
    String formatNumber(float number) {
        float num = (float) Math.round(number * 100) / 100;
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(num).replace(",", ".");
    }
    public void clearCart() {
        products.clear();
        total = 0;
    }

    public void updateProducts() {
        try (Connection connection = DbConnection.connect()){
            for (Product product : products.keySet()) {
                String query = "UPDATE Products SET stock = stock - " + products.get(product) + " where barcode = " + product.barcode;
                connection.createStatement().executeUpdate(query);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
