package org.example;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, SQLException {
        DbConnection db = new DbConnection();
        db.connect();
        ProductScanner scanner = new ProductScanner();

        while (true) {
            scanner.scan();
        }
    }
}

