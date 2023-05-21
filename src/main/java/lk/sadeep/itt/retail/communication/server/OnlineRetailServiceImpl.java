package lk.sadeep.itt.retail.communication.server;

import io.grpc.stub.StreamObserver;
import lk.sadeep.iit.retail.communication.grpc.generated.*;
import lk.sadeep.itt.retail.core.Item;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnlineRetailServiceImpl extends OnlineRetailServiceGrpc.OnlineRetailServiceImplBase {

    @Override
    public void updateStockCheckout(UpdateStockCheckoutRequest request, StreamObserver<UpdateStockCheckoutResponse> responseObserver) {

        System.out.println("\nItem stock update request received.");

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

        // TODO : update the item sock
        try {
            boolean isItemsAvailable = Item.checkoutUpdateStock(requestedQtys, customerId, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\nItem stock synced successfully.");
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
