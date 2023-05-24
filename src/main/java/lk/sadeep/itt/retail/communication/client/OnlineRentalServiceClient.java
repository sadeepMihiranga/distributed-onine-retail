package lk.sadeep.itt.retail.communication.client;

import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lk.sadeep.iit.retail.communication.grpc.generated.*;
import lk.sadeep.itt.retail.communication.dto.UpdateStockCheckoutRequestDTO;
import lk.sadeep.itt.retail.core.Item;

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

    public void updateInventoryCheckout(Long customerId, List<UpdateStockCheckoutRequestDTO> updateStockCheckoutRequestDTOList) {

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

        System.out.println("\nIs item stock Updated : " + updateStockCheckoutResponse.getIsUpdated());
    }

    public void addNewItem(Item item) {

        initializeConnection();

        AddNewItemRequest addNewItemRequest = AddNewItemRequest.newBuilder()
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

        System.out.println("\nNew item add response  : " + addNewItemResponse.getResponseMessage());
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
