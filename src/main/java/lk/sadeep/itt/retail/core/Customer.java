package lk.sadeep.itt.retail.core;

import lk.sadeep.itt.retail.core.constants.UserType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.*;

public class Customer {

    private Long id;
    private User user;

    private Cart cart = new Cart();

    public Customer() {
    }

    public Customer(Long id) {
        this.id = id;
    }

    private static final Object customerIdLock = new Object();

    private static List<Customer> customerList = new ArrayList<>();

    public void showCustomerMenu() throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("\nCUSTOMER MENU\n");

        int selectedOption;
        do
        {
            System.out.println("-------------------------------------\n");
            System.out.println("1 - View Item List");
            System.out.println("2 - Search an Item");
            System.out.println("3 - View Cart");
            System.out.println("4 - Edit Profile");
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
                case 1 : showCategorizedItems(id); break;
                case 2 : new Item().searchItemPage(); break;
                case 3 : new Cart().viewCart(id); break;
                case 4 : System.out.println("Edit Profile Page"); break;
                case 5 : new MainMenu().showMainMenu(); break;
                case 6 : MainMenu.exit(); break;
                default: System.out.println("Invalid option ! Please try again.");
            }
        } while(selectedOption != 6);
    }

    public void showCategorizedItems(Long customerId) {
        Item item = new Item(UserType.CUSTOMER, customerId);
        try {
            item.showCategorizedItems();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void showAllCustomers() throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("\nALL CUSTOMERS\n");

        System.out.println("----------------------------------------");
        System.out.printf("%-10s \t %-20s \n", "Customer Id", "Username");
        System.out.println("----------------------------------------\n");

        for (Customer customer : customerList) {
            System.out.printf("%-10d \t %-20s \n", customer.getId(), customer.getUser().getUsername());
        }

        System.out.println("----------------------------------------\n");
        System.out.println("1 - Previous Menu");
        System.out.println("2 - Main Menu");
        System.out.println("\n-------------------------------------\n");
        System.out.print("Select an option : ");

        int selectedOption;

        try {
            selectedOption = Integer.parseInt(br.readLine());
        } catch (NumberFormatException | IOException ex) {
            selectedOption = 3;
        }

        if(selectedOption == 3) {
            System.out.println("Invalid option ! Please try again.");
            showAllCustomers();
        }

        if(selectedOption == 2) {
            new MainMenu().showMainMenu();
        }

        if(selectedOption == 1) {
            new Admin().showAdminMenu();
        }
    }

    public static void addToCart(Long customerId, Long itemId, int requestedQty) {

        // check requested qty can supply
        Item item = Item.checkItemAvailability(itemId, requestedQty);

        if(item == null) {
            System.out.println("\nCannot serve requested quantity");
        }

        BigDecimal totalPrice = item.getItemPrice().multiply(BigDecimal.valueOf(requestedQty));

        Item addedItem = new Item();
        addedItem.setItemId(item.getItemId());
        addedItem.setItemCode(item.getItemCode());
        addedItem.setItemName(item.getItemName());
        addedItem.setItemDescription(item.getItemDescription());
        addedItem.setItemPrice(totalPrice);
        addedItem.setQuantity(Long.valueOf(requestedQty));

        Cart.addToCartList(customerId, addedItem);
        System.out.println("\n"+requestedQty+" '"+item.getItemName()+"' has been added to your cart.");
    }

    public synchronized static void checkoutTest(Long customerId, Long itemId, int requestedQty) throws IOException {

        Map<Long, Integer> requestedQtys = new HashMap<>();
        requestedQtys.put(itemId, requestedQty);

        Item.checkoutUpdateStock(requestedQtys, customerId, false);

        System.out.println("\nCustomer " +customerId+ ", Checkout succeeded.");
    }

    public static Optional<Customer> addNewCustomer(Customer newCustomer) {

        /** create user and map to the admin */
        newCustomer.getUser().setUserType(UserType.CUSTOMER);
        Optional<User> optionalUser = User.createUser(newCustomer.getUser());
        newCustomer.setUser(optionalUser.get());

        final Long availableId = getNextAvailableId();

        newCustomer.setId(availableId);
        customerList.add(newCustomer);

        return findCustomerById(availableId);
    }

    public static Long getNextAvailableId() {

        synchronized (customerIdLock) {
            Long newCustomerId = 1l;

            if(customerList.isEmpty()) {
                return newCustomerId;
            }

            Customer customerWithMaxId = Collections.max(customerList, Comparator.comparing(customer -> customer.getId()));

            if(customerWithMaxId != null) {
                newCustomerId = customerWithMaxId.getId() + 1;
            }

            return newCustomerId;
        }
    }

    public static Optional<Customer> findCustomerById(Long customerId) {
        return customerList.stream()
                .filter(customer -> customer.getId().equals(customerId))
                .findFirst();
    }

    public static Optional<Customer> findCustomerByUserId(Long userId) {
        return customerList.stream()
                .filter(customer -> customer.getUser().getUserId().equals(userId))
                .findFirst();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
