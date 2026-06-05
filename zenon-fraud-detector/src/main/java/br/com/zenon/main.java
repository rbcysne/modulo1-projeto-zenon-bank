package br.com.zenon;

import java.math.BigDecimal;

import static br.com.zenon.TransactionType.*;

public class main {

    static void main() {
        Transaction t1 = new Transaction(1, PAYMENT, new BigDecimal("9839.64"),
                new TransactionCustomer("C1231006815", new BigDecimal("170136.0"), new BigDecimal("160296.36")),
                new TransactionCustomer("M1979787155", new BigDecimal("0.0"), new BigDecimal("0.0")), false, false);

        Transaction t2 = new Transaction(743, PAYMENT, new BigDecimal("850002.52"),
                new TransactionCustomer("C1280323807", new BigDecimal("850002.52"), new BigDecimal("0.0")),
                new TransactionCustomer("C873221189", new BigDecimal("6510099"), new BigDecimal("7360101")), true, false);

        IO.println(t1);
        IO.println(t2);
    }
}
