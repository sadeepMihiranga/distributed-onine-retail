package lk.sadeep.itt.retail;

import com.google.gson.Gson;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lk.sadeep.iit.NameServiceClient;
import lk.sadeep.itt.retail.communication.server.OnlineRetailServiceImpl;
import lk.sadeep.itt.retail.core.Item;
import lk.sadeep.itt.retail.core.MainMenu;
import lk.sadeep.itt.retail.custom.nodemanager.ActiveNodeKeeper;
import lk.sadeep.itt.retail.synchronization.DistributedLock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProjectEntryPointHandler {


    private static boolean isAnc = false;
    private static String host;
    private static int port;
    private static final String NAME_SERVICE_ADDRESS = "http://localhost:2379";
    private static final String ZOOKEEPER_URL = "localhost:2181";

    public static int getPort() {
        return port;
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        /** read host and port from input params  */
        validateCmd(args);

        if(isAnc) {
            ActiveNodeKeeper.startNodeChecker();
            return;
        }

        Server server = ServerBuilder
                .forPort(port)
                .addService(new OnlineRetailServiceImpl())
                .build();
        server.start();
        System.out.println("\nActive node started and ready to accept requests on port " + port);

        DistributedLock.setZooKeeperURL(ZOOKEEPER_URL);

        /** register node location in the name service */
        registerNameService(port, host);

        /** insert some item when starting the application */
        insertItems();

        /** start the online retail application */
        try {
            new MainMenu().showMainMenu();
        } catch (IOException ex) {}

        server.awaitTermination();
    }

    private static void registerNameService(int serverPort, String host) throws IOException {
        NameServiceClient client = new NameServiceClient(NAME_SERVICE_ADDRESS);
        client.registerService("OnlineRetailService_"+serverPort, host, serverPort, "tcp");
    }

    private static void validateCmd(String[] args) {

        if(args[0].equals("anc")) {
            if (args.length != 1) {
                System.exit(1);
            }

            isAnc = true;

        } else {
            if (args.length != 2) {
                System.out.println("Param needs <host> <port>");
                System.exit(1);
            }

            host = args[0];

            String stringPort = args[1];

            port = Integer.valueOf(stringPort);
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
