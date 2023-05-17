package lk.sadeep.itt.retail.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.*;

public class Cart {

    private Long customerId;

    private BigDecimal cartValue;
    private static List<Item> itemList = new ArrayList<>();

    public Cart() {
    }

    public Cart(Long customerId, List<Item> itemList) {
        this.customerId = customerId;
        this.itemList = itemList;
    }

    private static List<Cart> activeCartList = new ArrayList<>();

    private static final Object cartListLock = new Object();

    public void viewCart(Long customerId) throws IOException {

        Optional<Cart> optionalCart = null;

        optionalCart = activeCartList.stream()
                .filter(cart -> cart.getCustomerId().equals(customerId))
                .findFirst();

        if(optionalCart.isEmpty()) {
            System.out.println("\nCart is empty");
            return;
        }

        BigDecimal currentCartValue = BigDecimal.ZERO;

        System.out.println("\nITEMS IN THE CART\n");

        System.out.println("--------------------------------------------------------------------------------------------");
        System.out.printf("%-10s \t %-10s \t %-20s \t %-20s \t %-20s\n", "Id", "Code", "Name", "Quantity", "Price");
        System.out.println("--------------------------------------------------------------------------------------------");

        for(Item item : optionalCart.get().getItemList()) {
            System.out.printf("%-10d \t %-10s \t %-20s \t %-20d \t %-20f\n",item.getItemId(), item.getItemCode(), item.getItemName(), item.getQuantity(), item.getItemPrice());

            currentCartValue = currentCartValue.add(item.getItemPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        System.out.println("--------------------------------------------------------------------------------------------\n");

        System.out.println("Total Cart Amount : " + currentCartValue);
        optionalCart.get().setCartValue(currentCartValue);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int selectedOption;
        boolean isItemSelected = false;
        String input = null;

        do
        {
            System.out.println("-------------------------------------\n");
            System.out.println("Enter '#' Followed by Item Id to Remove the Item from Cart. Ex: #1");
            System.out.println("1 - Checkout");
            System.out.println("2 - Logout");
            System.out.println("3 - Exit");
            System.out.println("\n-------------------------------------\n");
            System.out.print("Select an option : ");

            input = br.readLine();

            if(input.contains("#")) {
                // remove item from the cart
                if(!input.matches("#\\d+")) {
                    System.out.println("\nInvalid input. Try again. Ex: #1");
                    viewCart(customerId);
                }
                isItemSelected = true;
                break;
            } else {

                try {
                    selectedOption = Integer.parseInt(input);
                } catch (NumberFormatException ex) {
                    selectedOption = 4;
                }

                switch (selectedOption)
                {
                    case 1 : confirmCheckout(optionalCart.get()); break;
                    case 2 : new MainMenu().showMainMenu(); break;
                    case 3 : MainMenu.exit(); break;
                    default: System.out.println("Invalid option ! Please try again.");
                }
            }
        } while(selectedOption != 3);

        if(isItemSelected) { /** if item selected to remove */

            String stringItemId = input.split("#")[1];

            // validate entered item id

            Optional<Item> itemOptional = activeCartList.stream()
                    .filter(cart -> cart.getCustomerId().equals(customerId))
                    .findFirst().get().getItemList().stream()
                    .filter(item -> item.getItemId().equals(Long.valueOf(stringItemId)))
                    .findFirst();

            optionalCart.get().getItemList().remove(itemOptional.get());
            System.out.println("\nItem removed from your cart.");

            viewCart(customerId);
        }
    }

    private static void confirmCheckout(Cart cart) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("\nYour cart total is : " + cart.getCartValue());

        int selectedOption;
        do
        {
            System.out.println("-------------------------------------\n");
            System.out.println("1 - Confirm Checkout");
            System.out.println("2 - View Cart");
            System.out.println("3 - Main Menu");
            System.out.println("4 - Logout");
            System.out.println("5 - Exit");
            System.out.println("\n-------------------------------------\n");
            System.out.print("Select an option : ");

            try {
                selectedOption = Integer.parseInt(br.readLine());
            } catch (NumberFormatException | IOException ex) {
                selectedOption = 6;
            }

            switch (selectedOption)
            {
                case 1 : checkout(cart); break;
                case 2 : new Cart().viewCart(cart.getCustomerId()); break;
                case 3 : new Customer().showCustomerMenu(); break;
                case 4 : new MainMenu().showMainMenu(); break;
                case 5 : MainMenu.exit(); break;
                default: System.out.println("Invalid option ! Please try again.");
            }
        } while(selectedOption != 5);
    }

    private static void checkout(Cart cart) throws IOException {

        synchronized(cartListLock) {
            activeCartList.remove(cart);

            Map<Long, Integer> requestedQtys = new HashMap<>();

            for(Item item : cart.getItemList()) {
                requestedQtys.put(item.getItemId(), item.getQuantity().intValue());
            }

            // update the stock
            boolean isItemsAvailable = Item.checkoutUpdateStock(requestedQtys, cart.getCustomerId());

            if(!isItemsAvailable) {
                System.out.println("\nThank you for your purchase.");
            }
        }

        System.out.println("\nThank you for your purchase.");
    }

    public static void addToCartList(Long customerId, Item item) {

        List<Cart> synchronizedCarts = Collections.synchronizedList(activeCartList);

        Optional<Cart> optionalCart = synchronizedCarts.stream()
                .filter(cart -> cart.getCustomerId().equals(customerId))
                .findFirst();

        if(optionalCart.isEmpty()) { /** create virtual cart for the customer */
            synchronizedCarts.add(new Cart(customerId, Arrays.asList(item)));
        } else { /** if cart already assigned to the customer */

            Optional<Item> cartItemOptional = optionalCart.get().getItemList().stream()
                    .filter(cartItem -> cartItem.getItemId().equals(item.getItemId()))
                    .findFirst();

            if(cartItemOptional.isPresent()) { /** reset quantity and price to newly selected values */

                final Long totalQty = cartItemOptional.get().getQuantity() + item.getQuantity();
                final BigDecimal totalPrice = cartItemOptional.get().getItemPrice().add(item.getItemPrice());

                cartItemOptional.get().setQuantity(totalQty);
                cartItemOptional.get().setItemPrice(totalPrice);

            } else { /** add new item to the cart */
                for(Cart cart : synchronizedCarts) {
                    if(cart.getCustomerId().equals(customerId)) {
                        ArrayList<Item> items = new ArrayList<>(cart.getItemList());
                        items.add(item);

                        cart.setItemList(items);
                    }
                }
            }
        }
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    public BigDecimal getCartValue() {
        return cartValue;
    }

    public void setCartValue(BigDecimal cartValue) {
        this.cartValue = cartValue;
    }
}
