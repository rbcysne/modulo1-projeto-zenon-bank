package br.com.zenon;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static br.com.zenon.TransactionType.*;

public class main {

    static void main() {
        Transaction t1 = new Transaction(1, PAYMENT, new BigDecimal("9839.64"),
                new TransactionCustomer("C1231006815", new BigDecimal("170136.0"), new BigDecimal("160296.36")),
                new TransactionCustomer("M1979787155", new BigDecimal("0.0"), new BigDecimal("0.0")), false, false);

        Transaction t2 = new Transaction(743, PAYMENT, new BigDecimal("850002.52"),
                new TransactionCustomer("C1280323807", new BigDecimal("850002.52"), new BigDecimal("0.0")),
                new TransactionCustomer("C873221189", new BigDecimal("6510099"), new BigDecimal("7360101")), true, false);

//        IO.println(t1);
//        IO.println(t2);


        String fileName = "/Users/rommelcysne/IdeaProjects/unipds/modulo1-projeto-zenon-bank/data/PS_20174392719_1491204439457_log.csv";

        TransactionIngestor ingestor = new TransactionIngestor();
        List<Transaction> transactions = ingestor.readFile(fileName);

        IO.println("transactions size: " + transactions.size());
        transactions.stream().limit(10).forEach(IO::println);

//        String fileNameBadData = "/Users/rommelcysne/IdeaProjects/unipds/modulo1-projeto-zenon-bank/data/paysim_with_bad_data.csv";
//
//        IO.println("---------------- Transactions with bad data: -------------------");
//        List<Transaction> transactionsBadData = ingestor.readFile(fileNameBadData);
//        IO.println("total geral: " + transactionsBadData.size());
//        transactionsBadData.forEach(IO::println);

        IO.println("---------------- Fraud Analyzer: -------------------");

        FraudAnalyzer fraudAnalyzer = new FraudAnalyzer(transactions);
//        IO.println("total de fraudes: ");
//        transactions.stream().filter(Transaction::isFraud).forEach(IO::println);
//        fraudAnalyzer.getFrauds(transactions);
        //Conta apenas transações que isFraud = true
        long frauds = fraudAnalyzer.countFrauds();
        IO.println("Total de fraudes: " + frauds);

        List<Transaction> highestFrauds = fraudAnalyzer.getHighestFrauds(3);
        IO.println("Top 3 frauds: ");
        highestFrauds
                .stream()
                .map(Transaction::amount)
                .forEach(IO::println);

        List<String> mostSuspiciousCustomers = fraudAnalyzer.getSuspiciousCustomers(5);
        IO.println("Top 5 suspicious customers: ");
        mostSuspiciousCustomers.forEach(IO::println);

        BigDecimal totalFraudLoss = fraudAnalyzer.calculateTotalFraudLoss();
        IO.println("Total fraud loss: " + totalFraudLoss);

        Map<TransactionType, Long> fraudByType = fraudAnalyzer.getTotalFraudsByType();
        IO.println("Fraudes por tipo: ");
        fraudByType.forEach((type, count) -> IO.println(type + ": " + count));
    }
}
