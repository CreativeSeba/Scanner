package org.example;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
        }

    }

    private long getCardNumber() {
        System.out.println("Enter card number: ");

        try {
            long cardNumber = Long.parseLong(scn.nextLine());
            String cardNumberCopy = String.valueOf(cardNumber);
            if (cardNumberCopy.length() == 16)
                return cardNumber;
        } catch (NumberFormatException ignored) {
        }

        return 0;
    }

    private String getExpirationDate() {
        System.out.println("Enter card's expiration date (YYYY-MM-DD): ");
        String expirationDate = scn.nextLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            LocalDate date = LocalDate.parse(expirationDate, formatter);
            return expirationDate;
        } catch (DateTimeParseException e) {
            return null;
        }

    }

    private int getCVV() {
        System.out.println("Enter CVV: ");

        try {
            int cvv = Integer.parseInt(scn.nextLine());
            String cvvCopy = String.valueOf(cvv);
            if (cvvCopy.length() == 3) {
                return cvv;
            }
        } catch (NumberFormatException e) {}

        return 0;
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

        long cardNumber = getCardNumber();

        if (cardNumber != 0) {
            String expirationDate = getExpirationDate();
            if (expirationDate != null) {
                int cvv = getCVV();
                if (cvv != 0) {
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

                            boolean response = Boolean.parseBoolean(reader.readLine().trim());
                            // System.out.println("Server response: " + response);

                            if (response) {
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
                else {
                    System.out.println("Invalid cvv number");
                }
            }
            else {
                System.out.println("Invalid expiration date");
            }
        } else {
            System.out.println("Invalid card number");
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
