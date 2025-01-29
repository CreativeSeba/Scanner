package org.example;

public class Product {
    public String name;
    public double price;
    public long barcode;
    public int stock;

    public Product(String name, double price, long barcode, int stock) {
        this.name = name;
        this.price = price;
        this.barcode = barcode;
        this.stock = stock;
    }
}
