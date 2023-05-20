package lk.sadeep.itt.retail;

import com.google.gson.Gson;
import lk.sadeep.itt.retail.core.Customer;
import lk.sadeep.itt.retail.core.Item;
import lk.sadeep.itt.retail.custom.nodemanager.NodeInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Test {

    public static void main(String[] args) throws IOException {

        /*ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "start", "cmd", "/k", "etcdctl get --prefix OnlineRetailService_");
        builder.redirectErrorStream(true);
        builder.start();*/

        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec("etcdctl get --prefix OnlineRetailService_");

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        List<NodeInfo> allNodeInfo = new ArrayList<>();

        Gson gson = new Gson();

        // Read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
            if(s.startsWith("{") && s.endsWith("}")) {
                allNodeInfo.add(gson.fromJson(s, NodeInfo.class));
            }
        }

        System.out.println(allNodeInfo.size());

        /*BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

        // Read any errors from the attempted command
        System.out.println("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }*/

        /*Item item = new Item("IT001", "80Pgs Singled Rule", 1, "80Pgs Singled Rule",
                new BigDecimal(120.50), Long.valueOf(2));
        Optional<Item> item1 = item.addNewItem(item);

        // customer logging
        Customer customer1 = new Customer(1l);
        Customer customer2 = new Customer(2l);
        Customer customer3 = new Customer(3l);

        customer1.addToCart(customer1.getId(), item1.get().getItemId(), 1);
        customer2.addToCart(customer2.getId(), item1.get().getItemId(), 1);
        customer3.addToCart(customer3.getId(), item1.get().getItemId(), 1);

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
        customerT3.start();*/
    }
}
