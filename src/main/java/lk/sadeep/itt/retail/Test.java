package lk.sadeep.itt.retail;

import lk.sadeep.itt.retail.core.Customer;
import lk.sadeep.itt.retail.core.Item;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

public class Test {

    public static void main(String[] args) {

        Item item = new Item("IT001", "80Pgs Singled Rule", 1, "80Pgs Singled Rule",
                new BigDecimal(120.50), Long.valueOf(2));
        Optional<Item> item1 = item.addNewItem(item);

        // customer logging
        Customer customer1 = new Customer(1l);
        Customer customer2 = new Customer(2l);
        Customer customer3 = new Customer(3l);

        customer1.addToCart(customer1.getId(), item1.get().getItemId(), 1);
        customer2.addToCart(customer2.getId(), item1.get().getItemId(), 1);
        customer3.addToCart(customer3.getId(), item1.get().getItemId(), 1);

        /*customer3.checkoutTest(customer3.getId(), item1.get().getItemId(), 1);
        customer1.checkoutTest(customer1.getId(), item1.get().getItemId(), 1);
        customer2.checkoutTest(customer2.getId(), item1.get().getItemId(), 1);*/

        Thread customerT1 = new Thread(() -> {
            try {
                customer3.checkoutTest(customer3.getId(), item1.get().getItemId(), 1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Thread customerT2 = new Thread(() -> {
            try {
                customer1.checkoutTest(customer1.getId(), item1.get().getItemId(), 1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Thread customerT3 = new Thread(() -> {
            try {
                customer2.checkoutTest(customer2.getId(), item1.get().getItemId(), 1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        customerT1.start();
        customerT2.start();
        customerT3.start();
    }
}
