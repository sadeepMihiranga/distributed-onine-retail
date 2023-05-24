package lk.sadeep.itt.retail.core;

import lk.sadeep.itt.retail.ProjectEntryPointHandler;
import lk.sadeep.itt.retail.synchronization.DistributedLock;
import org.apache.zookeeper.KeeperException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DistributedLockHandler {

    public static DistributedLock acquireLock(String lockName) {
        DistributedLock lock = null;

        System.out.println("\nTrying distributed lock on to '"+lockName+"'");

        try {
            lock = new DistributedLock(lockName);
            lock.acquireLock();
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("\nProcess "+ ProjectEntryPointHandler.getPort()+" got the '"+lockName+"' lock at " + getCurrentTimeStamp());

        return lock;
    }

    public static void releaseLock(DistributedLock lock) {
        try {
            lock.releaseLock();
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("\nReleasing the '"+lock.getLockName()+"' lock " + getCurrentTimeStamp());
        lock = null;
    }

    private static String getCurrentTimeStamp() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis()));
    }
}
