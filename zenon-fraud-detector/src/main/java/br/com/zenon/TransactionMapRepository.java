package br.com.zenon;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TransactionMapRepository implements TransactionRepository{

    private final Map<String, Transaction> transactions;

    public TransactionMapRepository(List<Transaction> transactions) {
        this.transactions =
                transactions
                        .stream()
                        .collect(Collectors.toMap(transaction -> transaction.origin().name(),
                                Function.identity())); //poderia ser: transaction -> transaction (pega transaction e retorna ela mesma)
    }

    @Override
    public Optional<Transaction> getTransactionByNameOrig(String nameOrig) {

        return Optional.ofNullable(transactions.get(nameOrig));

    }
}
