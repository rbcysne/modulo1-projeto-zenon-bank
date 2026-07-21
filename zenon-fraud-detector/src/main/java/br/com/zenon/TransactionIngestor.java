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
import java.util.Scanner;

public class TransactionIngestor {

    public List<Transaction> readFile(String fileName) {

        List<Transaction> transactions = new ArrayList<>();

        Path path = Path.of(fileName);
        try {
            List<String> lines = Files.readAllLines(path);
            return lines.stream()
                    .skip(1)
                    .limit(1000)
                    .map(this::getTransaction)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

//        try (FileInputStream fis = new FileInputStream(fileName);
//             Scanner scanner = new Scanner(fis)) {
//
//            int line = 0;
//
//            while(scanner.hasNextLine()) {
//                String lineContent = scanner.nextLine();
//                line++;
//
//                if(line == 1) {
//                    continue;
//                }
//
//                if(line > 1001) {
//                    break;
//                }
//
//
//
//                Transaction transaction = getTransaction(lineContent);
//                transactions.add(transaction);
//
//            }
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

//        return transactions;

    }

    private Transaction getTransaction(String line) {
        String[] lineArray = line.split(",");
        int step =  Integer.parseInt(lineArray[0]);
        TransactionType type =  TransactionType.valueOf(lineArray[1]);
        BigDecimal amount = new BigDecimal(lineArray[2]);
        TransactionCustomer origin = new TransactionCustomer(lineArray[3], new BigDecimal(lineArray[4]), new BigDecimal(lineArray[5]));
        TransactionCustomer recipient =  new TransactionCustomer(lineArray[3], new BigDecimal(lineArray[4]), new BigDecimal(lineArray[5]));
        boolean isFraud = Boolean.parseBoolean(lineArray[6]);
        boolean isFlaggedFraud =  Boolean.parseBoolean(lineArray[7]);

        return new Transaction(step, type, amount, origin, recipient, isFraud, isFlaggedFraud);
    }
}
