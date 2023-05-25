package lk.sadeep.itt.retail.custom.nodemanager;

import com.google.gson.Gson;
import lk.sadeep.itt.retail.Constants;
import lk.sadeep.itt.retail.communication.client.OnlineRentalServiceClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class ActiveNodeKeeper {

    public static void startNodeChecker(Long statusCheckFreq) {

        System.out.println("ActiveNodeKeeper is doing it's job");

        TimerTask task = new TimerTask() {
            public void run() {
                List<NodeInfo> allNodeLocations = null;
                try {
                    allNodeLocations = getAllNodeLocations();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                for(NodeInfo nodeInfo : allNodeLocations) {
                    System.out.println("Checking Node via : " + nodeInfo.getPort() + " Active") ;

                    String nodeHealth = new OnlineRentalServiceClient(nodeInfo.getIp(), Integer.valueOf(nodeInfo.getPort())).checkNodeHealth();

                    if(nodeHealth == null || !nodeHealth.equals("ACTIVE")) {

                        System.out.println("Node in the " + nodeInfo.getPort() + " is inactive");

                        try {
                            Runtime.getRuntime().exec("etcdctl del OnlineRetailService_" + nodeInfo.getPort());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        };

        long delay = 0L;
        new Timer().schedule(task, delay, statusCheckFreq);
    }

    public static List<NodeInfo> getAllNodeLocations() throws IOException {

        Process proc =  Runtime.getRuntime().exec("etcdctl get --prefix " + Constants.SERVICE_NAME_BASE);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        List<NodeInfo> allNodeInfo = new ArrayList<>();
        Gson gson = new Gson();

        // Read the output from the command
        String s;
        while ((s = stdInput.readLine()) != null) {
            if(s.startsWith("{") && s.endsWith("}")) {
                allNodeInfo.add(gson.fromJson(s, NodeInfo.class));
            }
        }

        gson = null;
        stdInput = null;

        return allNodeInfo;
    }
}
