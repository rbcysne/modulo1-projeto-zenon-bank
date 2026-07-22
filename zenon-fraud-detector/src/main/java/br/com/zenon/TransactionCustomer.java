package br.com.zenon;

import java.math.BigDecimal;

public record TransactionCustomer(String name, BigDecimal oldBalance, BigDecimal newBalance) {

    public TransactionCustomer {
//        if(oldBalance.compareTo(BigDecimal.ZERO) < 0) {
        if(oldBalance.signum() < 0) {
            throw new IllegalArgumentException("oldBalance deve ser positivo: " + oldBalance);
        }

        if(newBalance.signum() < 0) {
            throw new IllegalArgumentException("newBalance deve ser positivo: " + newBalance);
        }

        if(name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name não pode ser nulo ou vazio");
        }
    }

}
