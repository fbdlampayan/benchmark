/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fbdl.grpc.benchmarkserver.services;

import com.fbdl.benchmark.grpc.EdgeSimResponse;
import com.fbdl.benchmark.grpc.LongLivedServiceGrpc;
import com.fbdl.benchmark.grpc.Notification;
import com.fbdl.benchmark.grpc.SimRequest;
import com.fbdl.benchmark.grpc.SimResponse;
import com.fbdl.benchmark.grpc.SubscribeRequest;
import com.fbdl.grpc.benchmarkserver.streams.EdgeSimResponseStream;
import com.fbdl.grpc.benchmarkserver.streams.SubscriptionStream;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author fbdl
 */
public class LongLived extends LongLivedServiceGrpc.LongLivedServiceImplBase {
    
    private final ConcurrentMap<String, StreamObserver<Notification>> subscribedHwMap;
    private final ConcurrentMap<String, SimRequest> simRequestMessageCache;
    private final ConcurrentMap<String, StreamObserver<SimResponse>> simResponseObserverMap;
    
    public LongLived(ConcurrentMap subscribedHwMap, ConcurrentMap simRequestMessageCache, ConcurrentMap simResponseObserverMap) {
        this.subscribedHwMap = subscribedHwMap;
        this.simRequestMessageCache = simRequestMessageCache;
        this.simResponseObserverMap = simResponseObserverMap;
    }
    
    @Override
    public StreamObserver<SubscribeRequest> subscribe(StreamObserver<Notification> responseObserver) {
        System.out.println("subscribe invoked");
        return new SubscriptionStream(subscribedHwMap, responseObserver);
    }
    
    @Override
    public StreamObserver<EdgeSimResponse> edgeProvisionSim(StreamObserver<SimRequest> responseObserver) {
        System.out.println("edge Provision Sim is called");
        return new EdgeSimResponseStream(simRequestMessageCache, simResponseObserverMap, responseObserver);
    }
}
