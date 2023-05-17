package lk.sadeep.itt.retail;

import lk.sadeep.itt.retail.core.Item;
import lk.sadeep.itt.retail.core.MainMenu;

import java.io.IOException;
import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) {

        /** insert some item when starting the application */
        insertItems();

        try {
            new MainMenu().showMainMenu();
        } catch (IOException ex) {}

        /*Thread mainMenuThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new lk.sadeep.iit.retail.core.MainMenu().showMainMenu();
                } catch (IOException ex) {}
            }
        });

        mainMenuThread.start();
        mainMenuThread.join();*/
    }

    private static void insertItems() {
        Item item1 = new Item("IT001", "80Pgs Singled Rule", 1, "80Pgs Singled Rule",
                new BigDecimal(120.50), Long.valueOf(10));
        Item item2 = new Item("IT002", "Blue Pen", 2, "Blue Pen",
                new BigDecimal(20), Long.valueOf(100));

        Item.addNewItem(item1);
        Item.addNewItem(item2);
    }
}