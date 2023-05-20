package lk.sadeep.itt.demo.server;

import io.grpc.stub.StreamObserver;
import lk.sadeep.iit.retail.communication.grpc.generated.BalanceServiceGrpc;
import lk.sadeep.iit.retail.communication.grpc.generated.CheckBalanceRequest;
import lk.sadeep.iit.retail.communication.grpc.generated.CheckBalanceResponse;

import java.util.Random;

public class BalanceServiceImpl extends BalanceServiceGrpc.BalanceServiceImplBase {

    private BankServer server;

    public BalanceServiceImpl(BankServer server){
        this.server = server;
    }

    @Override
    public void checkBalance(CheckBalanceRequest request, StreamObserver<CheckBalanceResponse> responseObserver) {

        String accountId = request.getAccountId();
        System.out.println("Request received..");
        double balance = getAccountBalance(accountId);
        CheckBalanceResponse response = CheckBalanceResponse.newBuilder()
                .setBalance(balance)
                .build();
        System.out.println("Responding, balance for account " + accountId + " is " + balance);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private double getAccountBalance(String accountId) {
        System.out.println("Checking balance for Account " + accountId);
        return new Random().nextDouble() * 10000;
    }

}
