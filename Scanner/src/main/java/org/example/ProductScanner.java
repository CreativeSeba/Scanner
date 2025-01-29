package org.example;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ProductScanner {
    Products products;
    Cart cart;

    public ProductScanner() {
        products = new Products();
        cart = new Cart();
    }

    public void scan() throws IOException, InterruptedException {
        System.out.println("Scan product");
        Scanner scn = new Scanner(System.in);
        String command = scn.nextLine();

        if (command.equals("reset")) {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            cart.clearCart();
            System.out.println("Cart has been reset");
        } else if (command.equals("pay")) {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            cart.printProducts();

            System.out.println("Enter card number: ");
            long cardNumber = Long.parseLong(scn.nextLine());
            System.out.println("Enter card's expiration date (YYYY-MM-DD): ");
            String expirationDate = scn.nextLine();
            System.out.println("Enter CVV: ");
            int cvv = Integer.parseInt(scn.nextLine());

            JsonObject json = new JsonObject();
            json.addProperty("cardNumber", cardNumber);
            json.addProperty("expirationDate", expirationDate);
            json.addProperty("cvv", cvv);
            json.addProperty("amount", cart.total);

            String urlString = "https://localhost:8080/api/payments";

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                System.out.println("Response Code: " + responseCode);

                /*System.out.println("Payment completed");
            cart.updateProducts();
            cart.clearCart();
            products.productsList.clear();
            products.getProducts();*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                long productBarcode = Long.parseLong(command);
                Product productTemp = products.searchProduct(productBarcode);
                if (productTemp != null) {
                    int currentQuantity = cart.products.getOrDefault(productTemp, 0);
                    if (currentQuantity < productTemp.stock) {
                        cart.addProduct(productTemp);
                        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                        cart.printProducts();
                    } else {
                        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                        cart.printProducts();
                        System.out.println("Product out of stock");
                    }

                } else {
                    new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                    cart.printProducts();
                    System.out.println("Product not found");
                }
            } catch (NumberFormatException e) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                cart.printProducts();
                System.out.println("Invalid command");
            }
        }

    }
}
