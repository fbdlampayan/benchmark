/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fbdl.grpc.benchmarkserver.services;

import com.fbdl.benchmark.grpc.LongLivedServiceGrpc;
import com.fbdl.benchmark.grpc.Notification;
import com.fbdl.benchmark.grpc.SubscribeRequest;
import com.fbdl.grpc.benchmarkserver.streams.SubscriptionStream;
import io.grpc.stub.StreamObserver;

/**
 *
 * @author fbdl
 */
public class LongLived extends LongLivedServiceGrpc.LongLivedServiceImplBase {
    
    @Override
    public StreamObserver<SubscribeRequest> subscribe(StreamObserver<Notification> responseObserver) {
        System.out.println("subscribe invoked");
        return new SubscriptionStream();
    }
}
