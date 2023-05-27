package lk.sadeep.itt.retail.communication.server;

import io.grpc.stub.StreamObserver;
import lk.sadeep.iit.retail.communication.grpc.generated.*;
import lk.sadeep.itt.retail.core.Customer;
import lk.sadeep.itt.retail.core.Item;
import lk.sadeep.itt.retail.core.MainMenu;
import lk.sadeep.itt.retail.core.User;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class handles all GRPC calls as a server and act as requested by the client
 * */
public class OnlineRetailServiceImpl extends OnlineRetailServiceGrpc.OnlineRetailServiceImplBase {

    @Override
    public void updateStockCheckout(UpdateStockCheckoutRequest request,
                                    StreamObserver<UpdateStockCheckoutResponse> responseObserver) {

        System.out.println("\nItem stock update GRPC request received.");

        syncItemStock(request.getCustomerId(), request.getUpdateStockCheckoutItemsList());

        UpdateStockCheckoutResponse updateStockCheckoutResponse = UpdateStockCheckoutResponse.newBuilder()
                .setIsUpdated(true)
                .setResponseMessage("Done")
                .build();

        responseObserver.onNext(updateStockCheckoutResponse);
        responseObserver.onCompleted();
    }

    private void syncItemStock(Long customerId, List<UpdateStockCheckoutItems> updateStockCheckoutItemsList) {

        System.out.println("\nSyncing item stock...");

        Map<Long, Integer> requestedQtys = new HashMap<>();

        for(UpdateStockCheckoutItems item : updateStockCheckoutItemsList) {
            requestedQtys.put(item.getItemId(), item.getRequestedQty());
        }

        try {
            boolean isItemsAvailable = Item.checkoutUpdateStock(requestedQtys, customerId, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\nItem stock synced successfully.");
    }

    @Override
    public void addNewItem(ItemRequest request, StreamObserver<AddNewItemResponse> responseObserver) {

        System.out.println("\nItem insert GRPC request received.");

        Item item = new Item(request.getItemId(), request.getCode(), request.getName(), Long.valueOf(request.getCategoryId()).intValue(),
                request.getDescription(), BigDecimal.valueOf(request.getPrice()), request.getQuantity());
        Item.addNewItem(item, false);

        System.out.println("\nItem insert synced successfully.");

        AddNewItemResponse addNewItemResponse = AddNewItemResponse.newBuilder()
                .setResponseMessage("SUCCESS")
                .build();

        responseObserver.onNext(addNewItemResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void registerUser(UserRequest request, StreamObserver<RegisterUserResponse> responseObserver) {

        System.out.println("\nCustomer insert GRPC request received.");

        User user = new User(request.getUsername(), request.getPassword());
        try {
            new MainMenu().registerCustomer(user);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\nCustomer insert synced successfully.");

        RegisterUserResponse registerUserResponse = RegisterUserResponse.newBuilder()
                .setResponseMessage("SUCCESS")
                .build();

        responseObserver.onNext(registerUserResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void syncItems(SyncItemsRequest request, StreamObserver<SyncItemsResponse> responseObserver) {

        System.out.println("\nSync items request received.");

        List<ItemRequest> itemRequestList = new ArrayList<>();

        for(Item item : Item.getItems()) {

            ItemRequest itemRequest = ItemRequest.newBuilder()
                    .setItemId(item.getItemId())
                    .setCode(item.getItemCode())
                    .setName(item.getItemName())
                    .setDescription(item.getItemDescription())
                    .setCategoryId(item.getItemCategory().getCategoryId())
                    .setPrice(item.getItemPrice().doubleValue())
                    .setQuantity(item.getQuantity())
                    .build();

            itemRequestList.add(itemRequest);
        }

        System.out.println("\nSync items request will response with " + itemRequestList.size() + " items.");

        SyncItemsResponse response = SyncItemsResponse.newBuilder()
                .addAllItems(itemRequestList)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void syncCustomers(SyncCustomersRequest request, StreamObserver<SyncCustomersResponse> responseObserver) {

        System.out.println("\nSync customers request received.");

        List<CustomerRPCO> customerList = new ArrayList<>();

        for(Customer customer : Customer.getCustomers()) {

            UserRequest user = UserRequest.newBuilder()
                    .setUserId(customer.getUser().getUserId())
                    .setUsername(customer.getUser().getUsername())
                    .setPassword(customer.getUser().getPassword())
                    .build();

            CustomerRPCO customerRPCO = CustomerRPCO.newBuilder()
                    .setCustomerId(customer.getId())
                    .setUser(user)
                    .build();

            customerList.add(customerRPCO);
        }

        System.out.println("\nSync customers request will response with " + customerList.size() + " customers.");

        SyncCustomersResponse response = SyncCustomersResponse.newBuilder()
                .addAllCustomers(customerList)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void checkNodeHealth(CheckNodeHealthRequest request, StreamObserver<CheckNodeHealthResponse> responseObserver) {

        CheckNodeHealthResponse response = CheckNodeHealthResponse.newBuilder()
                .setResponseMessage("ACTIVE")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
