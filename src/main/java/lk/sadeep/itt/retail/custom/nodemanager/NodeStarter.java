package lk.sadeep.itt.retail.custom.nodemanager;

import java.awt.*;
import java.io.Console;
import java.io.IOException;

public class NodeStarter {

    public static void startActiveNodeKeeper() throws IOException, InterruptedException {

        final String jarLocation = "C:\\Users\\sadde\\IdeaProjects\\distributed-onine-retail\\target";
        final String jarRunCmd = "java -jar distributed-onine-retail-1.0-SNAPSHOT-shaded.jar anc";

        runCmd(jarLocation, jarRunCmd);
    }

    public static void runCmd(String jarLocation, String jarRunCmd) throws IOException, InterruptedException {
        Console console = System.console();
        if(console == null && !GraphicsEnvironment.isHeadless()) {

            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "start", "cmd", "/k", "cd \""+jarLocation+"\" && " + jarRunCmd);
            builder.redirectErrorStream(true);
            builder.start();

        } else {
            System.out.println("Program has ended, please type 'exit' to close the console");
        }
    }
}
