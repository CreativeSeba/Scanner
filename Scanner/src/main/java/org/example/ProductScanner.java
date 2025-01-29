package org.example;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ProductScanner {
    Products products;
    Cart cart;
    Scanner scn;

    public ProductScanner() {
        products = new Products();
        cart = new Cart();
        scn = new Scanner(System.in);
    }

    public void scan() throws IOException, InterruptedException {
        System.out.println("Scan product");
        String command = scn.nextLine();

        switch (command) {
            case "reset":
                reset();
                break;
            case "pay":
                pay();
                break;
            default:
                searchProduct(command);
                break;
        }

    }

    private void clearConsole() throws IOException, InterruptedException {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
    }

    private void reset() throws IOException, InterruptedException {
        clearConsole();
        cart.clearCart();
        System.out.println("Cart has been reset");
    }

    private void pay() throws IOException, InterruptedException {
        clearConsole();
        cart.printProducts();

        System.out.println("Enter card number: ");
        long cardNumber = Long.parseLong(scn.nextLine());
        System.out.println("Enter card's expiration date (YYYY-MM-DD): ");
        String expirationDate = scn.nextLine();
        System.out.println("Enter CVV: ");
        int cvv = Integer.parseInt(scn.nextLine());

        String json = "{ \"cardNumber\": " + cardNumber +
                ", \"expirationDate\": \"" + expirationDate + "\"" +
                ", \"cvv\": " + cvv +
                ", \"amount\": " + cart.total + " }";

        String urlString = "http://localhost:8080/api/payments";

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getResponseCode() >= 200 && connection.getResponseCode() < 300 ?
                            connection.getInputStream() : connection.getErrorStream()))) {

                String response = reader.readLine().trim();
                System.out.println("Server response: " + response);

                if (Integer.parseInt(response) == 200) {
                    System.out.println("Payment completed");
                    cart.updateProducts();
                    cart.clearCart();
                    products.productsList.clear();
                    products.getProducts();
                } else {
                    System.out.println("Payment failed");
                }
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void searchProduct(String command) throws IOException, InterruptedException {
        try {
            long productBarcode = Long.parseLong(command);
            Product productTemp = products.searchProduct(productBarcode);
            if (productTemp != null) {
                int currentQuantity = cart.products.getOrDefault(productTemp, 0);
                if (currentQuantity < productTemp.stock) {
                    cart.addProduct(productTemp);
                    clearConsole();
                    cart.printProducts();
                } else {
                    clearConsole();
                    cart.printProducts();
                    System.out.println("Product out of stock");
                }

            } else {
                clearConsole();
                cart.printProducts();
                System.out.println("Product not found");
            }
        } catch (NumberFormatException | IOException | InterruptedException e) {
            clearConsole();
            cart.printProducts();
            System.out.println("Invalid command");
        }
    }
}
