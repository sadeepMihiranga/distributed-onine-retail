package lk.sadeep.itt.retail.communication;

import java.util.ArrayList;
import java.util.List;

public class OtherNodesLocationStore {

    private int nodeId;
    private String host;
    private int port;

    private static List<OtherNodesLocationStore> activeNodeLocations = new ArrayList<>();

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public static List<OtherNodesLocationStore> getActiveNodeLocations() {
        return activeNodeLocations;
    }

    public static void setActiveNodeLocations(List<OtherNodesLocationStore> activeNodeLocations) {
        OtherNodesLocationStore.activeNodeLocations = activeNodeLocations;
    }

    public static void showNodeLocations() {

        System.out.println("Stored Node's Locations");

        activeNodeLocations.forEach(otherNodesLocationStore -> {
            System.out.println(otherNodesLocationStore.getHost() + " : " + otherNodesLocationStore.getPort());
        });
    }
}
