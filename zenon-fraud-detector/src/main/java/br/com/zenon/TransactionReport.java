package br.com.zenon;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class TransactionReport {

    private record ReportTransaction(BigDecimal amount, boolean isFraud) {

    }

    public record Statistics(long totalTransactions, long totalFrauds, BigDecimal totalAmount) {

        private static final Statistics ZERO_STATISTICS = new Statistics(0, 0, BigDecimal.ZERO);

        //será a função acumuladora usada no reduce(); os tipos de argumento são Statistics e ReportTransaction
        //e a implamentação faz a soma de valores long e BigDecimal
        private Statistics addRe(ReportTransaction rt) {
            return new Statistics(
                    totalTransactions + 1,
                    totalFrauds + (rt.isFraud ? 1 : 0),
                    totalAmount.add(rt.amount)
            );
        }

        //será o combinador usado no reduce()
        private Statistics add(Statistics other) {
            return new Statistics(
                    totalTransactions + other.totalTransactions,
                    totalFrauds + other.totalFrauds,
                    totalAmount.add(other.totalAmount)
            );
        }
    }

    public Statistics generateReport(String fileName) {

        Path path = Path.of(fileName);
        try (Stream<String> lines = Files.lines(path)){
            return lines
                    .skip(1)
                    .map(this::getReportTransaction)
                    .filter(t -> t.isPresent()) //Optional.isPresent() -> method reference
                    .map(t -> t.get())
                    .reduce(Statistics.ZERO_STATISTICS, Statistics::addRe, Statistics::add);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler arquivo: " + fileName, e);
        }
    }

    private Optional<ReportTransaction> getReportTransaction(String line) {

        try {
            String[] lineArray = line.split(",");

            if(lineArray[2].equals("null") || lineArray[2].trim().isEmpty()) {
                throw new IllegalArgumentException("Amount não pode ser nulo ou vazio");
            }
            BigDecimal amount = new BigDecimal(lineArray[2]);
            boolean isFraud = "1".equals(lineArray[9]);

            return Optional.of(new ReportTransaction(amount, isFraud));
        } catch (Exception e) {
            System.err.println("Erro a ler linha: " + line + " | " + e);
            return Optional.empty();
        }
    }
}
