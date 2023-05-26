package lk.sadeep.itt.retail.communication.client;

import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lk.sadeep.iit.retail.communication.grpc.generated.*;
import lk.sadeep.itt.retail.communication.dto.UpdateStockCheckoutRequestDTO;
import lk.sadeep.itt.retail.core.Customer;
import lk.sadeep.itt.retail.core.Item;
import lk.sadeep.itt.retail.core.ItemCategory;
import lk.sadeep.itt.retail.core.User;
import lk.sadeep.itt.retail.core.constants.UserType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OnlineRentalServiceClient {

    private String host = null;
    private int port = -1;

    private ManagedChannel channel = null;
    private OnlineRetailServiceGrpc.OnlineRetailServiceBlockingStub onlineRetailServiceClientStub;
    public OnlineRentalServiceClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private void initializeConnection () {
        System.out.println("\nInitializing Connecting to server at " + host + ":" + port);
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        onlineRetailServiceClientStub = OnlineRetailServiceGrpc.newBlockingStub(channel);
    }

    private void closeConnection() {
        channel.shutdown();
    }

    public void updateInventoryCheckoutSync(Long customerId,
                                            List<UpdateStockCheckoutRequestDTO> updateStockCheckoutRequestDTOList) {

        initializeConnection();

        List<UpdateStockCheckoutItems> updateStockCheckoutItemsList = new ArrayList<>();

        for(UpdateStockCheckoutRequestDTO updateStockCheckoutRequestDTO : updateStockCheckoutRequestDTOList) {

            UpdateStockCheckoutItems updateStockCheckoutItems = UpdateStockCheckoutItems.newBuilder()
                    .setItemId(updateStockCheckoutRequestDTO.getItemId())
                    .setRequestedQty(updateStockCheckoutRequestDTO.getRequestedQty())
                    .build();

            updateStockCheckoutItemsList.add(updateStockCheckoutItems);
        }

        UpdateStockCheckoutRequest updateStockCheckoutRequest = UpdateStockCheckoutRequest
                .newBuilder()
                .setCustomerId(customerId)
                .addAllUpdateStockCheckoutItems(updateStockCheckoutItemsList)
                .build();

        UpdateStockCheckoutResponse updateStockCheckoutResponse = onlineRetailServiceClientStub
                .withDeadline(Deadline.after(5, TimeUnit.SECONDS))
                .withWaitForReady()
                .updateStockCheckout(updateStockCheckoutRequest);

        closeConnection();

        System.out.println("\nIs item stock GRPC Updated : " + updateStockCheckoutResponse.getIsUpdated());
    }

    public void addNewItemSync(Item item) {

        initializeConnection();

        ItemRequest addNewItemRequest = ItemRequest.newBuilder()
                        .setItemId(item.getItemId())
                        .setCode(item.getItemCode())
                        .setCategoryId(item.getItemCategory().getCategoryId())
                        .setName(item.getItemName())
                        .setDescription(item.getItemDescription())
                        .setPrice(item.getItemPrice().doubleValue())
                        .setQuantity(item.getQuantity().intValue())
                .build();

        AddNewItemResponse addNewItemResponse = onlineRetailServiceClientStub
                .withDeadline(Deadline.after(5, TimeUnit.SECONDS))
                .withWaitForReady()
                .addNewItem(addNewItemRequest);

        closeConnection();

        System.out.println("\nNew item add GRPC response  : " + addNewItemResponse.getResponseMessage());
    }

    public void registerUserSync(User user) {

        initializeConnection();

        UserRequest registerUserRequest = UserRequest.newBuilder()
                .setUsername(user.getUsername())
                .setPassword(user.getPassword())
                .build();

        RegisterUserResponse registerUserResponse = onlineRetailServiceClientStub
                .withDeadline(Deadline.after(5, TimeUnit.SECONDS))
                .withWaitForReady()
                .registerUser(registerUserRequest);

        closeConnection();

        System.out.println("\nRegister user GRPC response  : " + registerUserResponse.getResponseMessage());
    }

    public void syncItems() {

        initializeConnection();

        SyncItemsRequest request = SyncItemsRequest.newBuilder().build();

        SyncItemsResponse response = onlineRetailServiceClientStub
                .withDeadline(Deadline.after(1, TimeUnit.SECONDS))
                .withWaitForReady()
                .syncItems(request);

        closeConnection();

        System.out.println("\nItems sync success with  : " + response.getItemsList().size());

        for(ItemRequest itemRequest : response.getItemsList()) {

            Item item = new Item();
            item.setItemId(itemRequest.getItemId());
            item.setItemCode(itemRequest.getCode());
            item.setItemName(itemRequest.getName());
            item.setItemDescription(itemRequest.getDescription());
            item.setItemPrice(BigDecimal.valueOf(itemRequest.getPrice()));
            item.setQuantity(itemRequest.getQuantity());
            item.setItemCategory(ItemCategory.findById(Long.valueOf(itemRequest.getCategoryId()).intValue()).get());

            Item.getItems().add(item);
        }
    }

    public void syncCustomers() {

        initializeConnection();

        SyncCustomersRequest syncCustomersRequest = SyncCustomersRequest.newBuilder().build();

        SyncCustomersResponse response = onlineRetailServiceClientStub
                .withDeadline(Deadline.after(1, TimeUnit.SECONDS))
                .withWaitForReady()
                .syncCustomers(syncCustomersRequest);

        closeConnection();

        System.out.println("\nCustomers sync success with  : " + response.getCustomersList().size());

        for(CustomerRPCO customerRPCO : response.getCustomersList()) {

            User user = new User(customerRPCO.getUser().getUserId(), customerRPCO.getUser().getUsername(),
                    customerRPCO.getUser().getPassword(), UserType.CUSTOMER);
            User.getUsers().add(user);

            Customer customer = new Customer();
            customer.setId(customerRPCO.getCustomerId());
            customer.setUser(user);
            Customer.getCustomers().add(customer);
        }
    }

    public String checkNodeHealth() {

        initializeConnection();

        CheckNodeHealthRequest request = CheckNodeHealthRequest
                .newBuilder()
                .setRequest("IS_ACTIVE")
                .build();

        CheckNodeHealthResponse response = null;

        try {

            response = onlineRetailServiceClientStub
                    .withDeadline(Deadline.after(1, TimeUnit.SECONDS))
                    .withWaitForReady()
                    .checkNodeHealth(request);

            closeConnection();

        } catch (Exception e) {
            return "INACTIVE";
        }

        return response.getResponseMessage();
    }
}
