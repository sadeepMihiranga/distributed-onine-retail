package lk.sadeep.itt;

import java.awt.*;
import java.io.Console;
import java.io.IOException;

public class NodeStarter {

    public static void main(String[] args) throws InterruptedException, IOException {

        startServer();
        startClients(2);

        System.exit(0);
    }

    public static void startServer() throws IOException, InterruptedException {

        final String jarLocation = "C:\\Users\\sadde\\IdeaProjects\\distributed-onine-retail\\target";
        final String jarRunCmd = "java -jar distributed-onine-retail-1.0-SNAPSHOT-shaded.jar S localhost 11436";

        runCmd(jarLocation, jarRunCmd);
    }

    public static void startClients(int noOfClients) throws IOException, InterruptedException {

        while (noOfClients != 0) {

            final String jarLocation = "C:\\Users\\sadde\\IdeaProjects\\distributed-onine-retail\\target";
            final String jarRunCmd = "java -jar distributed-onine-retail-1.0-SNAPSHOT-shaded.jar C localhost 11436";

            runCmd(jarLocation, jarRunCmd);

            noOfClients--;
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
