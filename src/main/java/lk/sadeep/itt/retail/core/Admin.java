package lk.sadeep.itt.retail.core;

import lk.sadeep.itt.retail.core.constants.UserType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Admin {

    private Long id;
    private User user;

    private static final Object adminIdLock = new Object();

    private static List<Admin> adminList = new ArrayList<>();

    public void showAdminMenu() throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("\nADMIN MENU\n");

        int selectedOption;
        do
        {
            System.out.println("-------------------------------------\n");
            System.out.println("1 - Manage Items");
            System.out.println("2 - View Customers");
            System.out.println("3 - Add Customer");
            System.out.println("4 - Remove Customer");
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
                case 1 : new Item().showManageItemMenu(UserType.ADMIN); break;
                case 2 : new Customer().showAllCustomers(); break;
                case 3 : System.out.println("Add Customer"); break;
                case 4 : System.out.println("Remove Customer"); break;
                case 5 : new MainMenu().showMainMenu(); break;
                case 6 : MainMenu.exit(); break;
                default: System.out.println("Invalid option ! Please try again.");
            }
        } while(selectedOption != 6);
    }

    public static Optional<Admin> addNewAdmin(Admin newAdmin) {

        /** create user and map to the admin */
        newAdmin.getUser().setUserType(UserType.ADMIN);
        Optional<User> optionalUser = User.createUser(newAdmin.getUser());
        newAdmin.setUser(optionalUser.get());

        final Long availableId = getNextAvailableId();

        newAdmin.setId(availableId);
        adminList.add(newAdmin);

        return findAdminById(availableId);
    }

    public static Optional<Admin> findAdminById(Long adminId) {
        return adminList.stream()
                .filter(admin -> admin.getId().equals(adminId))
                .findFirst();
    }

    public static Long getNextAvailableId() {

        synchronized (adminIdLock) {
            Long newAdminId = 1l;

            if(adminList.isEmpty()) {
                return newAdminId;
            }

            Admin adminWithMaxId = Collections.max(adminList, Comparator.comparing(admin -> admin.getId()));

            if(adminWithMaxId != null) {
                newAdminId = adminWithMaxId.getId() + 1;
            }

            return newAdminId;
        }
    }

    public static void showAllAdmins() {
        for(Admin admin : adminList) {
            System.out.println("Admin Id " + admin.getId() + " found");
        }
    }

    private Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    private User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
