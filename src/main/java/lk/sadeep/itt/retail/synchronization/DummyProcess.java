package lk.sadeep.itt.retail.synchronization;

import lk.sadeep.itt.retail.core.Item;
import lk.sadeep.itt.retail.core.MainMenu;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class DummyProcess {

    /** By default, the Apache ZooKeeper server runs on port 2181 */
    public static final String ZOOKEEPER_URL = "127.0.0.1:2181";
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    public static void main(String[] args) {

        DistributedLock.setZooKeeperURL(ZOOKEEPER_URL);

        if (args.length < 1 ) {
            System.out.println("Usage DummyProcess requires <Lock Name to Acquire>");
            System.exit(1);
        }

        String lockName = args[0];
        System.out.println("Contesting to acquire lock " + lockName);

        try {

            DistributedLock lock = new DistributedLock(lockName);
            lock.acquireLock();
            System.out.println("I Got the lock at " + getCurrentTimeStamp());

            accessSharedResource();

            lock.releaseLock();
            System.out.println("Releasing the lock " + getCurrentTimeStamp());

        } catch (IOException | KeeperException | InterruptedException e) {
            System.out.println("Error while creating Distributed Lock myLock :" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String buildServerData(String IP, int port) {
        StringBuilder builder = new StringBuilder();
        builder.append(IP).append(":").append(port);
        return builder.toString();
    }

    private static void accessSharedResource() throws InterruptedException {
        Random r = new Random();
        long sleepDuration = Math.abs(r.nextInt() % 20);
        System.out.println("Accessing critical section. Time remaining : " + sleepDuration + " seconds.");
        Thread.sleep(sleepDuration * 1000);
    }

    private static String getCurrentTimeStamp() {
        return timeFormat.format(new Date(System.currentTimeMillis()));
    }
}
