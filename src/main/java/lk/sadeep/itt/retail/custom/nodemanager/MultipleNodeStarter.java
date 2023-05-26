package lk.sadeep.itt.retail.custom.nodemanager;

import lk.sadeep.itt.retail.PropertiesLoader;

import java.io.IOException;

public class MultipleNodeStarter extends NodeStarter {

    public static void main(String[] args) throws InterruptedException, IOException {

        final String startNodePort = PropertiesLoader.loadProperties().getProperty("start-port");

        // TODO : make sure pom.xml main class pointed to ProjectEntryPointHandler when building the jar
        startNodes(Integer.valueOf(startNodePort), 2);

        startActiveNodeKeeper();

        System.exit(0);
    }

    public static void startNodes(int startNodePort, int noOfNodes) throws IOException, InterruptedException {

        int nodeId = 0;

        do {
            final String jarLocation = "C:\\Users\\sadde\\IdeaProjects\\distributed-onine-retail\\target";
            final String jarRunCmd = "java -jar distributed-onine-retail-1.0-SNAPSHOT-shaded.jar 127.0.0.1 "+ (startNodePort + nodeId);

            runCmd(jarLocation, jarRunCmd);

            noOfNodes--;
            nodeId++;
        } while (noOfNodes != 0);
    }
}
