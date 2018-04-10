/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fbdl.grpc.benchmarkserver.services;

import com.fbdl.benchmark.grpc.Notification;
import com.fbdl.benchmark.grpc.Procedure;
import com.fbdl.benchmark.grpc.SimRequest;
import com.fbdl.benchmark.grpc.SimResponse;
import com.fbdl.benchmark.grpc.SmServiceGrpc;
import io.grpc.stub.StreamObserver;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author fbdl
 */
public class BmInternalService extends SmServiceGrpc.SmServiceImplBase {
    
    private final ConcurrentMap<String, StreamObserver<Notification>> subscribedHwMap;
    private final ConcurrentMap<String, SimRequest> simRequestMessageCache;
    private final ConcurrentMap<String, StreamObserver<SimResponse>> simResponseObserverMap;
    
    public BmInternalService(ConcurrentMap subscribedHwMap, ConcurrentMap simRequestMessageCache, ConcurrentMap simResponseObserverMap) {
        this.subscribedHwMap = subscribedHwMap;
        this.simRequestMessageCache = simRequestMessageCache;
        this.simResponseObserverMap = simResponseObserverMap;
    }
    
    @Override
    public void provisionSim(SimRequest request, StreamObserver<SimResponse> responseObserver) {
        System.out.println("provision sim invoked");
        pollingSupport(request, responseObserver);
    }
    
    private void pollingSupport(SimRequest request, StreamObserver<SimResponse> responseObserver) {
        System.out.println("polling way");
        String transactionId = request.getHwid();
        
        simRequestMessageCache.put(transactionId, request);
        simResponseObserverMap.put(transactionId, responseObserver);
    }

    private void longLivedSupport(SimRequest request, StreamObserver<SimResponse> responseObserver) {
        System.out.println("longlived way");
        String transactionId = UUID.randomUUID().toString();
        
        simRequestMessageCache.put(transactionId, request);
        simResponseObserverMap.put(transactionId, responseObserver);
        
        Notification notifyClient = Notification.newBuilder()
                .setTransactionId(transactionId)
                .setProcedure(Procedure.ADDUES)
                .build();
        
        StreamObserver<Notification> clientStub = subscribedHwMap.get(request.getHwid());
        clientStub.onNext(notifyClient);
        clientStub.onCompleted();
    }
    
}
