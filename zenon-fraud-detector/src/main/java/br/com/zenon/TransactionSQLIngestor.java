package br.com.zenon;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class TransactionSQLIngestor {

//    public static final int LINES_LIMIT = 10_000;
    public static final int LINE_BATCH_SIZE = 2_500;

    public void readFileAsBatch(String fileName, Consumer<List<Transaction>> batchConsumer) {
        List<Transaction> transactions = new ArrayList<>();

        Path path = Path.of(fileName);
        try (ExecutorService executorService = Executors.newFixedThreadPool(15);
             Stream<String> lines = Files.lines(path).skip(1)/*.limit(LINES_LIMIT)*/) {

            List<String> lineBatch = new ArrayList<>(LINE_BATCH_SIZE);
            Iterator<String> iterator = lines.iterator();
            while (iterator.hasNext()) {
                String line = iterator.next();
                lineBatch.add(line);

                if(lineBatch.size() >= LINE_BATCH_SIZE) {
                    IO.println("Executando batch ingestor...");
                    List<String> currentLineBatch = List.copyOf(lineBatch);
                    executorService.submit(() -> executeBatch(currentLineBatch, batchConsumer));
                    lineBatch.clear();
                }
            }

            if(!lineBatch.isEmpty()) {
                IO.println("Executando batch ingestor final...");
                List<String> currentLineBatch = List.copyOf(lineBatch);
                executorService.submit(() -> executeBatch(currentLineBatch, batchConsumer));
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler arquivo: " + fileName, e);
        }
    }

    //lê o arquivo e passa para o consumer registro por registro
    public void readFileAsStream(String fileName, Consumer<Transaction> consumer) {

        List<Transaction> transactions = new ArrayList<>();

        Path path = Path.of(fileName);
        try (Stream<String> lines = Files.lines(path)){
            lines
                    .skip(1)
//                    .limit(LINES_LIMIT)
                    .map(this::getTransaction)
                    //.filter(t -> t != null)  //Objects::nonNull -> não funciona com Optional
                    .filter(t -> t.isPresent()) //Optional.isPresent() -> method reference
                    .map(t -> t.get())
                    .forEach(consumer);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler arquivo: " + fileName, e);
        }
    }

    private Optional<Transaction> getTransaction(String line) {

        try {
            String[] lineArray = line.split(",");
            int step = Integer.parseInt(lineArray[0]);
            TransactionType type = TransactionType.valueOf(lineArray[1]);

            if(lineArray[2].equals("null") || lineArray[2].trim().isEmpty()) {
                throw new IllegalArgumentException("Amount não pode ser nulo ou vazio");
            }
            BigDecimal amount = new BigDecimal(lineArray[2]);
            TransactionCustomer origin = new TransactionCustomer(lineArray[3], new BigDecimal(lineArray[4]), new BigDecimal(lineArray[5]));
            TransactionCustomer recipient = new TransactionCustomer(lineArray[6], new BigDecimal(lineArray[7]), new BigDecimal(lineArray[8]));
            boolean isFraud = "1".equals(lineArray[9]);
            boolean isFlaggedFraud = "1".equals(lineArray[10]);

            return Optional.of(new Transaction(step, type, amount, origin, recipient, isFraud, isFlaggedFraud));
        } catch (Exception e) {
            System.err.println("Erro a ler linha: " + line + " | " + e);
            return Optional.empty();
        }
    }

    public void executeBatch(List<String> lineBatch, Consumer<List<Transaction>> batchConsumer) {
        List<Transaction> transactionsBatch = lineBatch
                .stream()
                .map(this::getTransaction)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        batchConsumer.accept(transactionsBatch);
    }
}
