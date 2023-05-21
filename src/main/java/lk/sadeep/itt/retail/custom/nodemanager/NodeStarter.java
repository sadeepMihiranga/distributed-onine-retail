package lk.sadeep.itt.retail.custom.nodemanager;

import java.awt.*;
import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NodeStarter {

    private static List<String> activeNodes = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException, IOException {

        // TODO : make sure pom.xml main class pointed to ProjectEntryPointHandler when building the jar
        startNodes(2);

        startActiveNodeKeeper();

        System.exit(0);
    }

    public static void startActiveNodeKeeper() throws IOException, InterruptedException {

        final String jarLocation = "C:\\Users\\sadde\\IdeaProjects\\distributed-onine-retail\\target";
        final String jarRunCmd = "java -jar distributed-onine-retail-1.0-SNAPSHOT-shaded.jar anc";

        runCmd(jarLocation, jarRunCmd);
    }

    public static void startNodes(int noOfNodes) throws IOException, InterruptedException {

        int nodeId = 0;

        while (noOfNodes != 0) {

            final String jarLocation = "C:\\Users\\sadde\\IdeaProjects\\distributed-onine-retail\\target";
            final String jarRunCmd = "java -jar distributed-onine-retail-1.0-SNAPSHOT-shaded.jar 127.0.0.1 "+ (11436 + noOfNodes);

            activeNodes.add(String.valueOf(11436 + noOfNodes));

            runCmd(jarLocation, jarRunCmd);

            noOfNodes--;
            nodeId++;
        }
    }

    public static void runCmd(String jarLocation, String jarRunCmd) throws IOException, InterruptedException {
        Console console = System.console();
        if(console == null && !GraphicsEnvironment.isHeadless()) {

            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "start", "cmd", "/k", "cd \""+jarLocation+"\" && " + jarRunCmd);
            builder.redirectErrorStream(true);
            builder.start();

        } else {
            NodeStarter.main(new String[0]);
            System.out.println("Program has ended, please type 'exit' to close the console");
        }
    }
}
