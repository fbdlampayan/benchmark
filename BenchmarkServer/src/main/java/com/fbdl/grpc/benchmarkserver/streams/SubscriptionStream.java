/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fbdl.grpc.benchmarkserver.streams;

import com.fbdl.benchmark.grpc.Notification;
import com.fbdl.benchmark.grpc.Procedure;
import com.fbdl.benchmark.grpc.SubscribeRequest;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fbdl
 */
public class SubscriptionStream implements StreamObserver<SubscribeRequest> {

    private final ConcurrentMap<String, StreamObserver<Notification>> subscribedHwMap;
    private final StreamObserver<Notification> responseObserverToClient;
    private String streamId;
    
    public SubscriptionStream(ConcurrentMap subscribedHwMap, StreamObserver<Notification> responseObserverToClient) {
        this.subscribedHwMap = subscribedHwMap;
        this.responseObserverToClient = responseObserverToClient;
    }
    
    private void setStreamId(String s) {
        this.streamId = s;
    }

    private String getStreamId() {
        return this.streamId;
    }

    
    @Override
    public void onNext(SubscribeRequest value) {
        subscribeHw(value.getHwid());
    }

    @Override
    public void onError(Throwable t) {
        System.out.println("subscribe onError called " + this.streamId + " - " + t.getMessage());
        t.printStackTrace();
    }

    @Override
    public void onCompleted() {
        System.out.println("subscribe onCompleted called " + this.streamId);
        responseObserverToClient.onCompleted();
        subscribedHwMap.remove(this.getStreamId());
    }

    private void subscribeHw(String hwid) {
        setStreamId(hwid);
        subscribedHwMap.put(hwid, responseObserverToClient);
    }
    
}
