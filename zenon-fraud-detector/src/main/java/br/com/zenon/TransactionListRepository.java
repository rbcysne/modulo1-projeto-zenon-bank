package br.com.zenon;

import javax.swing.plaf.basic.BasicTreeUI;
import java.util.List;
import java.util.Optional;

public class TransactionListRepository implements TransactionRepository{

    private final List<Transaction> transactions;

    public TransactionListRepository(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public Optional<Transaction> getTransactionByNameOrig(String nameOrig) {

        Optional<Transaction> transact = transactions
                .stream()
                .filter(transaction -> transaction.origin().name().equals(nameOrig))
                .findFirst();

        return transact;
    }

    @Override
    public void save(Transaction transaction) {
        transactions.add(transaction);
    }

}
