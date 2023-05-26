package lk.sadeep.itt.retail.custom.nodemanager;

import java.io.IOException;
import java.util.List;

public class SingleNodeStarter extends NodeStarter {

    public static void main(String[] args) throws IOException, InterruptedException {

        List<NodeInfo> allNodeLocations = ActiveNodeKeeper.getAllNodeLocations();

        Integer maxPort = 0;

        for(NodeInfo nodeInfo : allNodeLocations) {
            Integer port = Integer.valueOf(nodeInfo.getPort());

            if(maxPort < port) {
                maxPort = port;
            }
        }

        System.out.println(maxPort);

        final String jarLocation = "C:\\Users\\sadde\\IdeaProjects\\distributed-onine-retail\\target";
        final String jarRunCmd = "java -jar distributed-onine-retail-1.0-SNAPSHOT-shaded.jar new 127.0.0.1 "+ (maxPort + 1);

        runCmd(jarLocation, jarRunCmd);

        System.exit(0);
    }
}
