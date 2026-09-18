package br.com.zenon;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

import br.com.zenon.TransactionReport.Statistics;

public class ReportMain {

    public static void main(String[] args) {
        String language = "";

        Scanner scanner = new Scanner(System.in);
        IO.println("Digite o idioma (pt ou en): ");
        
        language = scanner.nextLine();
        scanner.close();

        Locale locale = Locale.of(language);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("report", locale);

        var integerFormatter = NumberFormat.getIntegerInstance(locale);
        var currencyFormatter = DecimalFormat.getCurrencyInstance(locale);
        currencyFormatter.setCurrency(Currency.getInstance("USD"));

        String msgTotalTransactions = resourceBundle.getString("label.total.transactions");
        String msgTotalFrauds = resourceBundle.getString("label.total.frauds");
        String msgTotalAmount = resourceBundle.getString("label.total.amount");

        String fileName = "/Users/rommelcysne/IdeaProjects/unipds/modulo1-projeto-zenon-bank/data/PS_20174392719_1491204439457_log.csv";

        TransactionReport ingestor = new TransactionReport();
        Statistics statistics = ingestor.generateReport(fileName);

        String fmtTotalTransactions = integerFormatter.format(statistics.totalTransactions());
        String fmtTotalFrauds = integerFormatter.format(statistics.totalFrauds());
        String fmtTotalAmount = currencyFormatter.format(statistics.totalAmount());

        IO.println("""
            %s: %s
            %s: %s
            %s: %s
            """.formatted(
                    msgTotalTransactions, fmtTotalTransactions,
                    msgTotalFrauds, fmtTotalFrauds,
                    msgTotalAmount, fmtTotalAmount)
        );

    }
}
