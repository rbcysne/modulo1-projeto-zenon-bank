package br.com.zenon;

import br.com.zenon.db.ConnectionFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class DBMain {

    static void main() {
        ConnectionFactory.getConnection();
        IO.println("Conectado com sucesso!");

        TransactionSqlRepository transactionSqlRepository = new TransactionSqlRepository();
        transactionSqlRepository.getTransactionByNameOrig("C1000001")
                .ifPresentOrElse(IO::println, () -> IO.println("Transação não encontrada para: C1000001"));

        transactionSqlRepository.getTransactionByNameOrig("C1234")
                .ifPresentOrElse(IO::println, () -> IO.println("Transação não encontrada para: C1234"));


        IO.println("---------------- Save transactions from file: -------------------");

        String fileName = "/Users/rommelcysne/IdeaProjects/unipds/modulo1-projeto-zenon-bank/data/PS_20174392719_1491204439457_log.csv";

//        TransactionType tipo = TransactionType.PAYMENT;

//        TransactionCustomer origin = new TransactionCustomer("C1000002", new BigDecimal("10000.0"), new BigDecimal("10000.00"));
//        TransactionCustomer recipient = new TransactionCustomer("C1000003", new BigDecimal("10001.0"), new BigDecimal("10001.00"));

//        Transaction t = new Transaction(2, tipo, new BigDecimal("5000.00"), origin, recipient,  false, false);
//        transactionSqlRepository.save(t);

        long inicioSql = System.nanoTime();
        TransactionIngestor ingestor = new TransactionIngestor();
        List<Transaction> transactions = ingestor.readFile(fileName);

        IO.println("transactions size: " + transactions.size());
        transactions.forEach(transactionSqlRepository::save);
        long fimSql = System.nanoTime();

        IO.println("Tempo de inclusão total (leitura de registros do arquivo e gravação no banco): " + ((fimSql - inicioSql) / 1_000_000.0) + " milissegundos");



    }
}
