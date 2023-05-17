package lk.sadeep.itt;

import java.awt.*;
import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NodeStarter {

    private static List<String> activeNodes = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException, IOException {

        startClients(3);
        startServer();

        System.exit(0);
    }

    public static void startServer() throws IOException, InterruptedException {

        String activeNodesPorts =  String.join(",", activeNodes);

        final String jarLocation = "C:\\Users\\sadde\\IdeaProjects\\distributed-onine-retail\\target";
        //final String jarRunCmd = "java -jar distributed-onine-retail-1.0-SNAPSHOT-shaded.jar L localhost 11436";
        final String jarRunCmd = "java -jar distributed-onine-retail-1.0-SNAPSHOT-shaded.jar L localhost " + activeNodesPorts;

        runCmd(jarLocation, jarRunCmd);
    }

    public static void startClients(int noOfClients) throws IOException, InterruptedException {

        int nodeId = 0;

        while (noOfClients != 0) {

            final String jarLocation = "C:\\Users\\sadde\\IdeaProjects\\distributed-onine-retail\\target";
            final String jarRunCmd = "java -jar distributed-onine-retail-1.0-SNAPSHOT-shaded.jar A localhost "+ (11436 + noOfClients);

            activeNodes.add(String.valueOf(11436 + noOfClients));

            runCmd(jarLocation, jarRunCmd);

            noOfClients--;
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
