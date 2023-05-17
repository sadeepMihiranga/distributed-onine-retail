package lk.sadeep.itt;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import lk.sadeep.itt.test.client.CheckBalanceServiceClient;
import lk.sadeep.itt.test.server.BalanceServiceImpl;

import java.io.IOException;

public class MainHandler {

    public static void main(String[] args) throws IOException, InterruptedException {

        String state = args[0]; // server or client ("S" or "C")
        String host = args[1];
        String port = args[2];

        if(state.equalsIgnoreCase("S")) {

            if (args.length != 3) {
                System.out.println("Param needs <app_type> <host> <port>");
                System.exit(1);
            }

            int serverPort = Integer.valueOf(port);

            Server server = ServerBuilder
                    .forPort(serverPort)
                    .addService(new BalanceServiceImpl())
                    .build();
            server.start();
            System.out.println("BankServer Started and ready to accept requests on port " + serverPort);
            server.awaitTermination();

        } else if (state.equalsIgnoreCase("C")) {

            if (args.length != 3) {
                System.out.println("Usage CheckBalanceServiceClient <app_type> <host> <port>");
                System.exit(1);
            }

            int serverPort = Integer.valueOf(port);

            CheckBalanceServiceClient client = new CheckBalanceServiceClient(host, serverPort);
            client.initializeConnection();
            client.processUserRequests();
            client.closeConnection();

        } else {
            return;
        }
    }
}
