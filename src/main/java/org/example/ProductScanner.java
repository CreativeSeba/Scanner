package org.example;

import java.io.IOException;
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
            System.out.println("Payment completed");
            cart.updateProducts();
            cart.clearCart();
            products.productsList.clear();
            products.getProducts();
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
