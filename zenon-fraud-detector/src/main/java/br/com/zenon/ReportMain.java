package br.com.zenon;

import java.util.List;
import br.com.zenon.TransactionReport.Statistics;

public class ReportMain {

    public static void main() {
        String fileName = "/Users/rommelcysne/IdeaProjects/unipds/modulo1-projeto-zenon-bank/data/PS_20174392719_1491204439457_log.csv";

        TransactionReport ingestor = new TransactionReport();
        Statistics statistics = ingestor.generateReport(fileName);

        IO.println("""
            Total de linhas: %d
            Total de fraudes: %d
            Valor total transacionado: %.2f
            """.formatted(statistics.totalTransactions(), statistics.totalFrauds(), statistics.totalAmount())
        );

    }
}
