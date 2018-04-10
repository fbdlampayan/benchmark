/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fbdl.grpc.benchmarkserver.services;

import com.fbdl.benchmark.grpc.EdgeSimPollingResponse;
import com.fbdl.benchmark.grpc.Notification;
import com.fbdl.benchmark.grpc.PollingNotification;
import com.fbdl.benchmark.grpc.PollingProcedure;
import com.fbdl.benchmark.grpc.PollingRequest;
import com.fbdl.benchmark.grpc.PollingServiceGrpc;
import com.fbdl.benchmark.grpc.SimRequest;
import com.fbdl.benchmark.grpc.SimResponse;
import com.fbdl.grpc.benchmarkserver.streams.EdgeSimPollingResponseStream;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author FBDL
 */
public class PollingServices extends PollingServiceGrpc.PollingServiceImplBase {
    
    private final ConcurrentMap<String, SimRequest> simRequestMessageCache;
    private final ConcurrentMap<String, StreamObserver<SimResponse>> simResponseObserverMap;
    
    public PollingServices(ConcurrentMap subscribedHwMap, ConcurrentMap simRequestMessageCache, ConcurrentMap simResponseObserverMap) {
        this.simRequestMessageCache = simRequestMessageCache;
        this.simResponseObserverMap = simResponseObserverMap;
    }
    
    @Override
    public void subscribe(PollingRequest request, StreamObserver<PollingNotification> responseObserver) {
        System.out.println("polling subscribed invoked " + request.toString());
        
        //check do you have a message for you
        if(!simRequestMessageCache.containsKey(request.getHwid())) {
            //if none, create your own response with "none" as transactionId
            PollingNotification noneNotif = PollingNotification.newBuilder().setTransactionId("none").build();
            responseObserver.onNext(noneNotif);
            responseObserver.onCompleted();
        }
        else {
            //start a procedure stream
            PollingNotification notifProcedure = PollingNotification.newBuilder().setTransactionId(request.getHwid()).setProcedure(PollingProcedure.ADDUES).build();
            responseObserver.onNext(notifProcedure);
            responseObserver.onCompleted();
        }
    }
    
    @Override
    public StreamObserver<EdgeSimPollingResponse> edgePollingProvisionSim(StreamObserver<SimRequest> responseObserver) {
        return new EdgeSimPollingResponseStream(simRequestMessageCache, simResponseObserverMap, responseObserver);
    }
    
}
