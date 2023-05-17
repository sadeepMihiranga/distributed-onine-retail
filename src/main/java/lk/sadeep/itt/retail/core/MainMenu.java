package lk.sadeep.itt.retail.core;

import lk.sadeep.itt.retail.core.constants.UserType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

public class MainMenu {

    public void showMainMenu() throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("\nWELCOME TO ONLINE RETAIL SHOPPING SYSTEM\n");

        int selectedOption;
        do
        {
            System.out.println("-------------------------------------\n");
            System.out.println("1 - Register as Admin");
            System.out.println("2 - Register as Customer");
            System.out.println("3 - Login");
            System.out.println("4 - Exit");
            System.out.println("\n-------------------------------------\n");
            System.out.print("Select an option : ");

            try {
                selectedOption = Integer.parseInt(br.readLine());
            } catch (NumberFormatException ex) {
                selectedOption = 5;
            }

            switch (selectedOption)
            {
                case 1 : registerAdmin(); break;
                case 2 : registerCustomer(); break;
                case 3 : login(); break;
                case 4 : exit(); break;
                default: System.out.println("Invalid option ! Please try again.");
            }
        } while(selectedOption != 4);
    }

    public void registerAdmin() throws IOException {

        User newUser = createUser(UserType.ADMIN);

        Admin newAdmin = new Admin();
        newAdmin.setUser(newUser);

        Admin.addNewAdmin(newAdmin);

        System.out.println("\nAdmin registered successfully !\n");
    }

    public void registerCustomer() throws IOException {

        User newUser = createUser(UserType.CUSTOMER);

        Customer newCustomer = new Customer();
        newCustomer.setUser(newUser);

        Customer.addNewCustomer(newCustomer);

        System.out.println("\nCustomer registered successfully !\n");
    }

    public User createUser(UserType userType) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String username, password;

        System.out.println("\nWELCOME TO "+userType+" REGISTRATION PAGE\n");
        System.out.println("-------------------------------------\n");

        if(userType == UserType.ADMIN) {
            System.out.println("Admin Id = " + Admin.getNextAvailableId());
        } else if (userType == UserType.CUSTOMER) {
            System.out.println("Customer Id = " + Customer.getNextAvailableId());
        }

        System.out.print("\nEnter username = ");
        username = br.readLine();
        System.out.print("Enter password = ");
        password = br.readLine();

        return new User(username, password);
    }

    public void login() throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("\nLOGIN PAGE\n");
        System.out.println("-------------------------------------\n");

        System.out.print("Enter username = ");
        String username = br.readLine();
        System.out.print("Enter password = ");
        String password = br.readLine();

        Optional<User> optionalUser = User.checkCredentials(username, password);

        if(!optionalUser.isPresent()) {
            handleInvalidLogin();
        } else {

            if(optionalUser.get().getUserType() == UserType.ADMIN) {
                new Admin().showAdminMenu();
            }

            if(optionalUser.get().getUserType() == UserType.CUSTOMER) {

                Optional<Customer> optionalCustomer = Customer.findCustomerByUserId(optionalUser.get().getUserId());

                Customer customer = new Customer(optionalCustomer.get().getId());
                customer.showCustomerMenu();
            }
        }
    }

    public void handleInvalidLogin() throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int selectedOption;
        do
        {
            System.out.println("\nINVALID LOGIN CREDENTIALS\n");
            System.out.println("-------------------------------------\n");

            System.out.println("-------------------------------------\n");
            System.out.println("1 - Try Login Again");
            System.out.println("2 - Main Menu");
            System.out.println("3 - Exit");
            System.out.println("\n-------------------------------------\n");
            System.out.print("Select an option : ");

            try {
                selectedOption = Integer.parseInt(br.readLine());
            } catch (NumberFormatException ex) {
                selectedOption = 4;
            }

            switch (selectedOption)
            {
                case 1 : login(); break;
                case 2 : showMainMenu(); break;
                case 3 : exit(); break;
                default: System.out.println("Invalid option ! Please try again.");
            }
        } while(selectedOption != 3);
    }

    public static void exit() {
        System.out.println("Thank you !");
        System.exit(0);
    }
}
