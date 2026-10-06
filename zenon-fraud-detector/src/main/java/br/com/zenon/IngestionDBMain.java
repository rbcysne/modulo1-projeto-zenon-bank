package br.com.zenon;

import br.com.zenon.db.ConnectionFactory;

import java.util.List;

public class IngestionDBMain {

    static void main() {
        ConnectionFactory.getConnection();
        IO.println("Conectado com sucesso!");

        TransactionSqlRepository transactionSqlRepository = new TransactionSqlRepository();

        IO.println("---------------- Save transactions from file: -------------------");

        String fileName = "/Users/rommelcysne/IdeaProjects/unipds/modulo1-projeto-zenon-bank/data/PS_20174392719_1491204439457_log.csv";

        long inicioSql = System.nanoTime();
        TransactionSQLIngestor ingestor = new TransactionSQLIngestor();
        ingestor.readFileAsBatch(fileName, transactionSqlRepository::saveAll);

        //IO.println("transactions size: " + transactions.size());
        // transactionSqlRepository.saveAll(transactions);
        long fimSql = System.nanoTime();

        IO.println("Tempo de inclusão total (leitura de registros do arquivo e gravação no banco): " + ((fimSql - inicioSql) / 1_000_000.0) + " milissegundos");



    }
}
