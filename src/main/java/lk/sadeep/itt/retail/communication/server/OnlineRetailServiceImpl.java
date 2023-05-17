package lk.sadeep.itt.retail.communication.server;

import io.grpc.stub.StreamObserver;
import lk.sadeep.iit.retail.communication.grpc.generated.CheckBalanceResponse;
import lk.sadeep.iit.retail.communication.grpc.generated.OnlineRetailServiceGrpc;
import lk.sadeep.iit.retail.communication.grpc.generated.UpdateStockCheckoutRequest;
import lk.sadeep.iit.retail.communication.grpc.generated.UpdateStockCheckoutResponse;

public class OnlineRetailServiceImpl extends OnlineRetailServiceGrpc.OnlineRetailServiceImplBase {

    @Override
    public void updateStockCheckout(UpdateStockCheckoutRequest request, StreamObserver<UpdateStockCheckoutResponse> responseObserver) {

        Long customerId = request.getCustomerId();
        System.out.println("Request received..");

        System.out.println("Customer id : " + customerId);

        UpdateStockCheckoutResponse updateStockCheckoutResponse = UpdateStockCheckoutResponse.newBuilder()
                .setIsUpdated(true)
                .setResponseMessage("Done")
                .build();

        responseObserver.onNext(updateStockCheckoutResponse);
        responseObserver.onCompleted();
    }
}
