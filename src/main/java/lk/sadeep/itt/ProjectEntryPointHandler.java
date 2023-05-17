package lk.sadeep.itt;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import lk.sadeep.itt.demo.client.CheckBalanceServiceClient;
import lk.sadeep.itt.retail.communication.OtherNodesLocationStore;
import lk.sadeep.itt.retail.communication.client.OnlineRentalServiceClient;
import lk.sadeep.itt.retail.communication.server.OnlineRetailServiceImpl;
import lk.sadeep.itt.retail.core.Item;
import lk.sadeep.itt.retail.core.MainMenu;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProjectEntryPointHandler {

    public static void main(String[] args) throws IOException, InterruptedException {

        String state = args[0]; // server or client ("S" or "C")
        String host = args[1];
        String port = args[2];

        if(state.equalsIgnoreCase("A")) { /** A - Active nodes */

            validateCmd(args);

            int serverPort = Integer.valueOf(port);

            /*Server server = ServerBuilder
                    .forPort(serverPort)
                    .addService(new BalanceServiceImpl())
                    .build();
            server.start();
            System.out.println("BankServer Started and ready to accept requests on port " + serverPort);
            server.awaitTermination();*/

            Server server = ServerBuilder
                    .forPort(serverPort)
                    .addService(new OnlineRetailServiceImpl())
                    .build();
            server.start();
            System.out.println("Active node started and ready to accept requests on port " + serverPort);
            server.awaitTermination();

        } else if (state.equalsIgnoreCase("L")) { /** Current leader node */

            validateCmd(args);

            List<String> ports = Arrays.asList(port.split("\\s*,\\s*"));

            List<OtherNodesLocationStore> otherNodesLocationStores = new ArrayList<>();

            for (String portString : ports) {
                OtherNodesLocationStore otherNodesLocationStore = new OtherNodesLocationStore();

                otherNodesLocationStore.setHost(host);
                otherNodesLocationStore.setPort(Integer.valueOf(portString));

                otherNodesLocationStores.add(otherNodesLocationStore);
            }

            OtherNodesLocationStore.setActiveNodeLocations(otherNodesLocationStores);
            OtherNodesLocationStore.showNodeLocations();

            /** insert some item when starting the application */
            insertItems();

            try {
                new MainMenu().showMainMenu();
            } catch (IOException ex) {}

            /*CheckBalanceServiceClient client = new CheckBalanceServiceClient(host, serverPort);
            client.initializeConnection();
            client.processUserRequests();
            client.closeConnection();*/

        } else {
            return;
        }
    }

    private static void validateCmd(String[] args) {
        if (args.length != 3) {
            System.out.println("Param needs <app_type> <host> <port> <node_id>");
            System.exit(1);
        }
    }

    private static void insertItems() {
        Item item1 = new Item("IT001", "80Pgs Singled Rule", 1, "80Pgs Singled Rule",
                new BigDecimal(120.50), Long.valueOf(10));
        Item item2 = new Item("IT002", "Blue Pen", 2, "Blue Pen",
                new BigDecimal(20), Long.valueOf(100));

        Item.addNewItem(item1);
        Item.addNewItem(item2);
    }
}
