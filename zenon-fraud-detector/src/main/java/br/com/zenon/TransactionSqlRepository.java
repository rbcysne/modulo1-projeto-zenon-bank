package br.com.zenon;

import br.com.zenon.db.ConnectionFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class TransactionSqlRepository implements TransactionRepository{

    private static final int JDBC_BATCH_SIZE = 1_000;

    @Override
    public Optional<Transaction> getTransactionByNameOrig(String nameOrig) {

        String origin = nameOrig;
        String sql = """
                SELECT id, step, `type`, amount, 
                       name_origin, old_balance_origin, new_balance_origin, 
                       name_recipient, old_balance_recipient, new_balance_recipient, 
                       is_fraud, is_flagged_fraud
                FROM zenon_frauds.transactions 
                WHERE name_origin = ?
                LIMIT 1
                """;

        try (Connection conn =  ConnectionFactory.getConnection()){
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nameOrig);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Transaction transaction = resultSetToTransaction(rs);
                    return Optional.of(transaction);
                } else {
                    IO.println("Transação não encontrada para origem: " + nameOrig);
                    return Optional.empty();
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //a cada registro de transação, abre uma conexão e grava no banco
    @Override
    public void save(Transaction transaction) {

        String sql = """
                insert into zenon_frauds.transactions 
                (step, type, amount, 
                name_origin, old_balance_origin, new_balance_origin, 
                name_recipient, old_balance_recipient, new_balance_recipient, 
                is_fraud, is_flagged_fraud) 
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?);
                """;

        try (Connection conn =  ConnectionFactory.getConnection()){
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, transaction.step());
            ps.setString(2, transaction.type().name());
            ps.setBigDecimal(3, transaction.amount());
            ps.setString(4, transaction.origin().name());
            ps.setBigDecimal(5, transaction.origin().oldBalance());
            ps.setBigDecimal(6, transaction.origin().newBalance());
            ps.setString(7, transaction.recipient().name());
            ps.setBigDecimal(8, transaction.recipient().oldBalance());
            ps.setBigDecimal(9, transaction.recipient().newBalance());
            ps.setBoolean(10, transaction.isFraud());
            ps.setBoolean(11, transaction.isFlaggedFraud());

            IO.println("Gravando registro");
            ps.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveAll(List<Transaction> transactions) {

        String sql = """
                insert into zenon_frauds.transactions 
                (step, type, amount, 
                name_origin, old_balance_origin, new_balance_origin, 
                name_recipient, old_balance_recipient, new_balance_recipient, 
                is_fraud, is_flagged_fraud) 
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?);
                """;

        int count = 0;
        try (Connection conn =  ConnectionFactory.getConnection()){
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                for (Transaction transaction : transactions ) {
                    ps.setInt(1, transaction.step());
                    ps.setString(2, transaction.type().name());
                    ps.setBigDecimal(3, transaction.amount());
                    ps.setString(4, transaction.origin().name());
                    ps.setBigDecimal(5, transaction.origin().oldBalance());
                    ps.setBigDecimal(6, transaction.origin().newBalance());
                    ps.setString(7, transaction.recipient().name());
                    ps.setBigDecimal(8, transaction.recipient().oldBalance());
                    ps.setBigDecimal(9, transaction.recipient().newBalance());
                    ps.setBoolean(10, transaction.isFraud());
                    ps.setBoolean(11, transaction.isFlaggedFraud());

                    ps.addBatch();
                    count++;

                    if(count % JDBC_BATCH_SIZE == 0) {
                        IO.println("Executando batch JDBC...");
                        ps.executeBatch();
                        conn.commit();
                    }
                }
                IO.println("Executando batch final JDBC...");
                ps.executeBatch();
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Erro ao executar rollback...", ex);
                }
                throw new RuntimeException("Erro ao salvar nova transação...", e);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Transaction resultSetToTransaction(ResultSet rs) {

        try {
            int step = rs.getInt("step");
            TransactionType type = TransactionType.valueOf(rs.getString("type"));
            BigDecimal amount = rs.getBigDecimal("amount");
            String nameOrigin = rs.getString("name_origin");
            BigDecimal oldBalanceOrigin = rs.getBigDecimal("old_balance_origin");
            BigDecimal newBalanceOrigin = rs.getBigDecimal("new_balance_origin");
            String nameRecipient = rs.getString("name_recipient");
            BigDecimal oldBalanceRecipient = rs.getBigDecimal("old_balance_recipient");
            BigDecimal newBalanceRecipient = rs.getBigDecimal("new_balance_recipient");

            TransactionCustomer origin = new TransactionCustomer(nameOrigin, oldBalanceOrigin, newBalanceOrigin);
            TransactionCustomer recipient = new TransactionCustomer(nameRecipient, oldBalanceRecipient, newBalanceRecipient);

            boolean isFraud = rs.getBoolean("is_fraud");
            boolean isFlaggedFraud = rs.getBoolean("is_flagged_fraud");

            return new Transaction(step, type, amount, origin, recipient, isFraud, isFlaggedFraud);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
