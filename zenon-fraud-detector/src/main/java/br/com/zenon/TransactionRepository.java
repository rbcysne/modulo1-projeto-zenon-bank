package br.com.zenon;

import java.util.Optional;

public interface TransactionRepository {

    Optional<Transaction> getTransactionByNameOrig(String nameOrig);

    void save(Transaction transaction);
}
