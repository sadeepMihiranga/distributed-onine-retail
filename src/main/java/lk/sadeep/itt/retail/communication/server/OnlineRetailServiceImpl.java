package lk.sadeep.itt.retail.communication.server;

import io.grpc.stub.StreamObserver;
import lk.sadeep.iit.retail.communication.grpc.generated.*;
import lk.sadeep.itt.retail.core.Item;
import lk.sadeep.itt.retail.core.MainMenu;
import lk.sadeep.itt.retail.core.User;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void addNewItem(AddNewItemRequest request, StreamObserver<AddNewItemResponse> responseObserver) {

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
    public void registerUser(RegisterUserRequest request, StreamObserver<RegisterUserResponse> responseObserver) {

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
    public void checkNodeHealth(CheckNodeHealthRequest request, StreamObserver<CheckNodeHealthResponse> responseObserver) {

        CheckNodeHealthResponse response = CheckNodeHealthResponse.newBuilder()
                .setResponseMessage("ACTIVE")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
