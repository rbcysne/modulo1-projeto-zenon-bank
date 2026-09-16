package br.com.zenon;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class TransactionIngestor {

    public static final int LINES_LIMIT = 100_000;

    public List<Transaction> readFile(String fileName) {

        List<Transaction> transactions = new ArrayList<>();

        Path path = Path.of(fileName);
        try {
            List<String> lines = Files.readAllLines(path);
            return lines.stream()
                    .skip(1)
                    .limit(LINES_LIMIT)
                    .map(this::getTransaction)
//                    .filter(t -> t != null)  //Objects::nonNull -> não funciona com Optional
                    .filter(t -> t.isPresent()) //Optional.isPresent() -> method reference
                    .map(t -> t.get())
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler arquivo: " + fileName, e);
        }
    }

    private Optional<Transaction> getTransaction(String line) {

        try {
            String[] lineArray = line.split(",");
            int step = Integer.parseInt(lineArray[0]);
            TransactionType type = TransactionType.valueOf(lineArray[1]);

            if(lineArray[2].equals("null") || lineArray[2].trim().isEmpty()) {
                throw new IllegalArgumentException("Amount não pode ser nulo ou vazio");
            }
            BigDecimal amount = new BigDecimal(lineArray[2]);
            TransactionCustomer origin = new TransactionCustomer(lineArray[3], new BigDecimal(lineArray[4]), new BigDecimal(lineArray[5]));
            TransactionCustomer recipient = new TransactionCustomer(lineArray[6], new BigDecimal(lineArray[7]), new BigDecimal(lineArray[8]));
            boolean isFraud = "1".equals(lineArray[9]);
            boolean isFlaggedFraud = "1".equals(lineArray[10]);

            return Optional.of(new Transaction(step, type, amount, origin, recipient, isFraud, isFlaggedFraud));
        } catch (Exception e) {
            System.err.println("Erro a ler linha: " + line + " | " + e);
            return Optional.empty();
        }
    }
}
