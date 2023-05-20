package lk.sadeep.itt.retail.core;

import com.google.gson.Gson;
import lk.sadeep.iit.NameServiceClient;
import lk.sadeep.itt.retail.custom.nodemanager.NodeInfo;
import lk.sadeep.itt.retail.custom.nodemanager.OtherNodesLocationStore;
import lk.sadeep.itt.retail.communication.client.OnlineRentalServiceClient;
import lk.sadeep.itt.retail.communication.dto.UpdateStockCheckoutRequestDTO;
import lk.sadeep.itt.retail.core.constants.UserType;
import lk.sadeep.itt.retail.custom.nodemanager.ProjectEntryPointHandler;
import lk.sadeep.itt.retail.synchronization.DistributedLock;
import org.apache.zookeeper.KeeperException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Item {

    private Long itemId;
    private String itemCode;
    private String itemName;

    private ItemCategory itemCategory;
    private String itemDescription;
    private BigDecimal itemPrice;
    private Long quantity;

    private UserType userType;
    private Long loggedCustomerId;

    private static List<Item> itemStore = new ArrayList<>();

    public static final String NAME_SERVICE_ADDRESS = "http://localhost:2379";

    public Item() {
    }

    public Item(UserType userType, Long loggedCustomerId) {
        this.userType = userType;
        this.loggedCustomerId = loggedCustomerId;
    }

    public Item(String itemCode, String itemName, int itemCategory, String itemDescription, BigDecimal itemPrice, Long quantity) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemPrice = itemPrice;
        this.quantity = quantity;
        this.itemCategory = ItemCategory.findById(itemCategory).get();
    }

    private static final Object itemStoreLock = new Object();
    private static final Object cartLock = new Object();

    public void showManageItemMenu(UserType userType) throws IOException {

        this.userType = userType;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("\nMANAGE ITEMS MENU\n");

        int selectedOption;
        do
        {
            System.out.println("-------------------------------------\n");
            System.out.println("1 - Add Item");
            System.out.println("2 - Remove Item");
            System.out.println("3 - Find an Item");
            System.out.println("4 - View All Items");
            System.out.println("5 - Logout");
            System.out.println("6 - Exit");
            System.out.println("\n-------------------------------------\n");
            System.out.print("Select an option : ");

            try {
                selectedOption = Integer.parseInt(br.readLine());
            } catch (NumberFormatException | IOException ex) {
                selectedOption = 7;
            }

            switch (selectedOption)
            {
                case 1 : addItemPage(); break;
                case 2 : removeItemPage(); break;
                case 3 : searchItemPage(); break;
                case 4 : System.out.println("View All Items"); break;
                case 5 : new MainMenu().showMainMenu(); break;
                case 6 : MainMenu.exit(); break;
                default: System.out.println("Invalid option ! Please try again.");
            }
        } while(selectedOption != 6);
    }

    public void showCategorizedItems() throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("\nITEM CATEGORIES\n");

        System.out.println("----------------------------------------");
        System.out.printf("%-10s \t %-20s \n", "Category Id", "Category");
        System.out.println("----------------------------------------\n");

        for (ItemCategory category : ItemCategory.getCategories()) {
            System.out.printf("%-10d \t %-20s \n",category.getCategoryId(), category.getCategoryName());
        }

        System.out.println("----------------------------------------\n");

        System.out.println("Press '0' to go to "+this.userType+" Menu, or");
        System.out.print("Choose a category id : ");

        int selectedOption = 1;

        try {
            selectedOption = Integer.parseInt(br.readLine());
        } catch (NumberFormatException | IOException ex) {
            showCategorizedItems();
        }

        if(selectedOption == 0) {
            if(this.userType == UserType.CUSTOMER) {
                new Customer().showCustomerMenu();
            } else if(this.userType == UserType.ADMIN) {
                new Admin().showAdminMenu();
            }
        }

        final Integer maxCategoryId = ItemCategory.getCategories()
                .stream()
                .max(Comparator.comparing(ItemCategory::getCategoryId))
                .get().getCategoryId();

        if(selectedOption > maxCategoryId) {
            System.out.println("Invalid category Id\n");
            showCategorizedItems();
        }

        viewAllItems(selectedOption);
    }

    private void viewAllItems(int categoryId) throws IOException
    {
        Optional<ItemCategory> category = ItemCategory.findById(categoryId);

        if(itemStore.isEmpty()) {
            System.out.println("Category not found !");
        } else {

            List<Item> itemList = findItemsByCategory(category.get().getCategoryName());

            if(itemList.isEmpty()) {
                System.out.println("No item found !");
                showCategorizedItems();
            }

            System.out.println("\nITEMS IN THE "+category.get().getCategoryName()+" CATEGORY\n");

            System.out.println("--------------------------------------------------------------------------------------------");
            System.out.printf("%-10s \t %-10s \t %-20s \t %-20s \t %-20s\n", "Id", "Code", "Name", "Quantity", "Price");
            System.out.println("--------------------------------------------------------------------------------------------");

            for(Item item : itemList) {
                if(item.getQuantity() != 0)
                    System.out.printf("%-10d \t %-10s \t %-20s \t %-20d \t %-20f\n",item.getItemId(), item.getItemCode(), item.getItemName(), item.getQuantity(), item.getItemPrice());
                else
                    System.out.printf("%-10d \t %-10s \t %-20s \t %-20s \t %-20f\n",item.getItemId(), item.getItemCode(), item.getItemName(), "NOT IN STOCK", item.getItemPrice());
            }

            System.out.println("--------------------------------------------------------------------------------------------");

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            int selectedOption;
            boolean isItemSelected = false;
            String input = null;

            do
            {
                System.out.println("\nSelect an Item Id with Quantity (Ex: 1*10)");
                System.out.println("1 - Category Menu");
                System.out.println("2 - Main Menu");
                System.out.println("3 - Logout");
                System.out.println("4 - Exit");
                System.out.println("\n-------------------------------------\n");
                System.out.print("Select an option : ");

                input = br.readLine();

                if(input.contains("*")) {
                    if(!input.matches("\\d+\\*\\d+")) {
                        System.out.println("\nInvalid input. Try again. (Ex: 1*10)");
                        viewAllItems(categoryId);
                    }
                    isItemSelected = true;
                    break;
                } else {
                    try {
                        selectedOption = Integer.parseInt(input);
                    } catch (NumberFormatException ex) {
                        selectedOption = 5;
                    }

                    switch (selectedOption)
                    {
                        case 1 : showCategorizedItems(); break;
                        case 2 : new Customer().showCustomerMenu(); break;
                        case 3 : new MainMenu().showMainMenu(); break;
                        case 4 : MainMenu.exit(); break;
                        default: System.out.println("Invalid option ! Please try again.");
                    }
                }
            } while(selectedOption != 4);

            if(isItemSelected) {
                String[] splitInput = input.split("\\*");

                if(splitInput.length != 2) {
                    System.out.println("\nInvalid input. Try again. (Ex: 1*10)");
                    viewAllItems(categoryId);
                }

                final String itemId = splitInput[0];
                final String requestedQty = splitInput[1];

                Optional<Item> itemOptional = findItemById(Long.valueOf(itemId));

                if(!itemOptional.isPresent()) {
                    System.out.println("Selected invalid item id, Try again");
                    viewAllItems(categoryId);
                }

                Customer.addToCart(loggedCustomerId, Long.valueOf(itemId), Integer.parseInt(requestedQty));
            }
        }
    }

    public void searchItemPage() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("\nSEARCH AN ITEM\n");
        System.out.println("-------------------------------------\n");

        System.out.println("1 - Search by Id");
        System.out.println("2 - Search by Code");
        System.out.println("3 - Search by Name");
        System.out.println("4 - Previous Menu");
        System.out.println("5 - Logout");
        System.out.println("6 - Exit");
        System.out.println("\n-------------------------------------\n");
        System.out.print("Select an option : ");

        int selectedOption;

        try {
            selectedOption = Integer.parseInt(br.readLine());
        } catch (NumberFormatException | IOException ex) {
            selectedOption = 7;
        }

        if(selectedOption == 6) {
            MainMenu.exit();
        }

        if(selectedOption == 7) {
            System.out.println("Invalid option ! Please try again.");
            searchItemPage();
        }

        if(selectedOption == 4) {
            if(this.userType == UserType.CUSTOMER) {
                new Customer().showCustomerMenu();
            } else if(this.userType == UserType.ADMIN) {
                new Admin().showAdminMenu();
            }
        }

        if(selectedOption == 5) {
            new MainMenu().showMainMenu();
        }

        /** search item by id */
        if(selectedOption == 1) {

            System.out.println("\nSEARCH AN ITEM BY ID\n");

            System.out.print("Enter item id = ");
            final String itemId = br.readLine();

            handleItemSearchResult(findItemById(Long.valueOf(itemId)), this.userType);
        }

        /** search item by item code */
        if(selectedOption == 2) {

            System.out.println("\nSEARCH AN ITEM BY ITEM CODE\n");

            System.out.print("Enter item code = ");
            final String itemCode = br.readLine();

            handleItemSearchResult(findItemByCode(itemCode), this.userType);
        }

        /** search item by item name */
        if(selectedOption == 3) {

            System.out.println("\nSEARCH AN ITEM BY ITEM NAME\n");

            System.out.print("Enter item name = ");
            final String itemName = br.readLine();

            handleItemSearchResult(findItemByName(itemName), this.userType);
        }
    }

    private void handleItemSearchResult(Optional<Item> itemOptional, UserType userType) throws IOException {

        if(itemOptional.isEmpty()) {
            System.out.println("No item found for the '"+itemName+"' ");
        }

        showItemInfo(itemOptional.get(), userType);
        searchItemPage();
    }

    private void showItemInfo(Item item, UserType userType) throws IOException {
        System.out.println("\nItem Id : " + item.getItemId());
        System.out.println("Item Code : " + item.getItemCode());
        System.out.println("Item Name : " + item.getItemName());
        System.out.println("Item Description : " + item.getItemDescription());
        System.out.println("Item Category : " + item.getItemCategory());

        if(userType == UserType.ADMIN) {
            System.out.println("Item Quantity : " + item.getQuantity());

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            int input;

            System.out.println("\nPress 0 to Search Menu or,");
            System.out.print("Enter newly received quantity to update : ");

            try {
                input = Integer.parseInt(br.readLine());
            } catch (NumberFormatException | IOException ex) {
                input = 0;
            }

            if(input == 0) {
                searchItemPage();
            }

            synchronized (itemStoreLock) {
                Optional<Item> itemOptional = findItemById(item.getItemId());

                if(!itemOptional.isPresent()) {
                    System.out.println("\nSelected invalid item id, Try again");
                    showItemInfo(item, userType);
                }

                itemOptional.get().setQuantity(itemOptional.get().getQuantity() + input);
                System.out.println("\nItem stock updated.");
            }
        }
    }

    private void addItemPage() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("\nADD NEW ITEM\n");
        System.out.println("-------------------------------------\n");

        System.out.print("\nEnter item code = ");
        String code = br.readLine();
        System.out.print("Enter item name = ");
        String name = br.readLine();
        System.out.print("Enter item category id = ");
        String categoryId = br.readLine();
        System.out.print("Enter item description = ");
        String description = br.readLine();
        System.out.print("Enter item price = ");
        String price = br.readLine();
        System.out.print("Enter item quantity = ");
        String quantity = br.readLine();

        // validate inputs

        // validate category with
        ItemCategory category = validateCategory(categoryId);

        Item item = new Item(code, name, category.getCategoryId(), description, new BigDecimal(price), Long.valueOf(quantity));

        Optional<Item> newItem = addNewItem(item);
        System.out.println("\nItem added successfully :"+ newItem.get().getItemId() +"\n");
    }

    private ItemCategory validateCategory(String categoryId) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Optional<ItemCategory> category = ItemCategory.findById(Integer.parseInt(categoryId));

        if(!category.isPresent()) {
            System.out.println("\nInvalid category id");
            System.out.println("Enter item category id = ");
            String categoryIdInput = br.readLine();

            validateCategory(categoryIdInput);
        }

        return category.get();
    }

    private void removeItemPage() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("\nREMOVE ITEM\n");
        System.out.println("-------------------------------------\n");

        System.out.print("\nEnter item id = ");
        final String itemId = br.readLine();

        Optional<Item> itemOptional = findItemById(Long.valueOf(itemId));

        if(itemOptional.isEmpty()) {
            System.out.println("Invalid item id. Please try again.");
            removeItemPage();
        }

        synchronized (itemStoreLock) {
            itemStore.remove(itemOptional.get());

            System.out.print("Item " + itemOptional.get().getItemName() + " removed successfully");
        }
    }

    public static Optional<Item> addNewItem(Item newItem) {

        final Long availableId = getNextAvailableId();

        synchronized (itemStoreLock) {

            newItem.setItemId(availableId);
            itemStore.add(newItem);

            return findItemById(availableId);
        }
    }

    public static Item checkItemAvailability(Long itemId, int requestedQty) {

        Optional<Item> itemOptional = findItemById(itemId);

        if(itemOptional.get().quantity < requestedQty) {
            return null;
        }
        return itemOptional.get();
    }

    public static String buildServerData(String IP, int port) {
        StringBuilder builder = new StringBuilder();
        builder.append(IP).append(":").append(port);
        return builder.toString();
    }


    /** this method shared across the distributed system */
    public synchronized static boolean checkoutUpdateStock(Map<Long, Integer> requestedQtys, Long customerId, boolean syncToOthers) throws IOException {

        boolean isItemsAvailable = true;
        List<UpdateStockCheckoutRequestDTO> updateStockCheckoutRequestDTOList = new ArrayList<>();

        final String lockName = "items_stock_lock";

        DistributedLock lock = null;

        if(syncToOthers) {
            System.out.println("\nTrying distributed lock on to '"+lockName+"'");

            try {
                lock = new DistributedLock(lockName);
                lock.acquireLock();
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("\nI Got the '"+lockName+"' lock at " + getCurrentTimeStamp());
        }

        for (Map.Entry<Long, Integer> itemEntry : requestedQtys.entrySet()) {

            final Long itemId = itemEntry.getKey();
            final int requestedQty = itemEntry.getValue();

            Optional<Item> itemOptional = findItemById(itemId);

            Item item = checkItemAvailability(itemId, requestedQty);

            if(item == null) {
                System.out.println("\nCannot serve "+itemOptional.get().getItemName()+", we only have "+itemOptional.get().getQuantity()+" items.");
                isItemsAvailable = false;
                break;
            }

            /** update the quantity */
            itemOptional.get().setQuantity(itemOptional.get().getQuantity() - requestedQty);

            itemStore.remove(itemOptional.get());
            itemStore.add(itemOptional.get());

            updateStockCheckoutRequestDTOList.add(new UpdateStockCheckoutRequestDTO(itemOptional.get().getItemId(), requestedQty));
        }

        // TODO : call GRPC call to other nodes to sync data
        if(syncToOthers) {
            try {

                NameServiceClient client = new NameServiceClient(NAME_SERVICE_ADDRESS);

                List<NodeInfo> allNodeLocations = getAllNodeLocations();

                for(NodeInfo nodeInfo : allNodeLocations) {
                    //NameServiceClient.ServiceDetails serviceDetails = client.findService("OnlineRetailService_11438");
                    NameServiceClient.ServiceDetails serviceDetails = client.findService("OnlineRetailService_" + nodeInfo.getPort());

                    /** accessing shared resource */
                    /*System.out.println("\nSending item sync request to : " + serviceDetails.getIPAddress() + ":" + serviceDetails.getPort());
                    new OnlineRentalServiceClient(serviceDetails.getIPAddress(), serviceDetails.getPort())
                            .processUserRequests(customerId, updateStockCheckoutRequestDTOList);*/

                    int port = ProjectEntryPointHandler.getPort();

                    if(port != Integer.valueOf(nodeInfo.getPort())) { /** sending syncing GRPC call for all other active nodes */
                        System.out.println("\nSending item sync request to : " + nodeInfo.getIp() + ":" + nodeInfo.getPort());
                        new OnlineRentalServiceClient(nodeInfo.getIp(), Integer.valueOf(nodeInfo.getPort()))
                                .processUserRequests(customerId, updateStockCheckoutRequestDTOList);
                    }
                }

            } catch (IOException | InterruptedException e) {
                System.out.println("Error while invoking GRPC call :" + e.getMessage());
                e.printStackTrace();
            }
        }

        if(syncToOthers) {
            try {
                lock.releaseLock();
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("\nReleasing the '"+lockName+"' lock " + getCurrentTimeStamp());
        }

        if(!isItemsAvailable) {
            new Cart().viewCart(customerId);
        }

        return isItemsAvailable;
    }

    private static List<NodeInfo> getAllNodeLocations() throws IOException {

        Process proc =  Runtime.getRuntime().exec("etcdctl get --prefix OnlineRetailService_");
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        List<NodeInfo> allNodeInfo = new ArrayList<>();
        Gson gson = new Gson();

        // Read the output from the command
        String s;
        while ((s = stdInput.readLine()) != null) {
            if(s.startsWith("{") && s.endsWith("}")) {
                allNodeInfo.add(gson.fromJson(s, NodeInfo.class));
            }
        }

        return allNodeInfo;
    }

    private static String getCurrentTimeStamp() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis()));
    }

    public static Optional<Item> findItemById(Long itemId) {
        return itemStore.stream()
                .filter(item -> item.getItemId().equals(itemId))
                .findFirst();
    }

    public static Optional<Item> findItemByCode(String itemCode) {
        return itemStore.stream()
                .filter(item -> item.getItemCode().equals(itemCode))
                .findFirst();
    }

    public static Optional<Item> findItemByName(String itemName) {
        return itemStore.stream()
                .filter(item -> item.getItemName().contains(itemName))
                .findFirst();
    }

    public static List<Item> findItemsByCategory(String categoryName) {
        return itemStore.stream()
                .filter(item -> item.getItemCategory().getCategoryName().contains(categoryName))
                .collect(Collectors.toList());
    }

    public static Long getNextAvailableId() {

        synchronized (itemStoreLock) {
            Long newItemId = 1l;

            if(itemStore.isEmpty()) {
                return newItemId;
            }

            Item itemWithMaxId = Collections.max(itemStore, Comparator.comparing(item -> item.getItemId()));

            if(itemWithMaxId != null) {
                newItemId = itemWithMaxId.getItemId() + 1;
            }

            return newItemId;
        }
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public ItemCategory getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(ItemCategory itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }
}
