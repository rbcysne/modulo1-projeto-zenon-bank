package br.com.zenon;

import java.math.BigDecimal;

public record Transaction(int step, TransactionType type, BigDecimal amount, TransactionCustomer origin,
                          TransactionCustomer recipient, boolean isFraud, boolean isFlaggedFraud) {

    public Transaction {
        if(step <= 0) {
            throw new IllegalArgumentException("Step deve ser maior que zero: " + step);
        }

        if(amount.signum() < 0) {
            throw new IllegalArgumentException("Amount deve ser positivo: " + amount);
        }
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "step=" + step +
                ", type=" + type +
                ", amount=" + amount +
                ", origin=" + origin +
                ", recipient=" + recipient +
                ", isFraud=" + isFraud +
                ", isFlaggedFraud=" + isFlaggedFraud +
                '}';
    }
}
