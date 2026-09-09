package br.com.zenon;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class FraudAnalyzer {

    private final List<Transaction> transactions;

    public FraudAnalyzer(List<Transaction> transactions) {
        Objects.requireNonNull(transactions);
        this.transactions = transactions;
    }

    public long countFrauds() {
        return transactions
                .stream()
                .filter(Transaction::isFraud)
                .count();
    }

    public List<Transaction> getHighestFrauds(int limit) {
        return transactions
                .stream()
                .filter(Transaction::isFraud)
                .sorted(Comparator.comparing(Transaction::amount).reversed())
                .limit(limit)
                .toList();
    }

    public List<String> getSuspiciousCustomers(int limit) {
        return transactions
                .stream()
                .filter(Transaction::isFraud)
                .sorted(Comparator.comparing(Transaction::amount).reversed())
                .map(transaction -> transaction.origin().name())
                .distinct()
                .limit(limit)
                .toList();
    }

    public void getFrauds() {
        transactions
               .stream()
                .filter(Transaction::isFraud)
                .forEach(IO::println);
    }

    public BigDecimal calculateTotalFraudLoss() {
        return transactions
                .stream()
                .filter(Transaction::isFraud)
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<TransactionType, Long> getTotalFraudsByType() {
        return transactions
                .stream()
                .filter(Transaction::isFraud)
                .collect(Collectors.groupingBy(Transaction::type, Collectors.counting()));
    }
}
