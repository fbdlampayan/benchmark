/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fbdl.grpc.benchmarkserver.streams;

import com.fbdl.benchmark.grpc.SubscribeRequest;
import io.grpc.stub.StreamObserver;

/**
 *
 * @author fbdl
 */
public class SubscriptionStream implements StreamObserver<SubscribeRequest> {

    @Override
    public void onNext(SubscribeRequest value) {
        System.out.println("subscribe onnext called");
    }

    @Override
    public void onError(Throwable t) {
        System.out.println("subscribe onError called");
    }

    @Override
    public void onCompleted() {
        System.out.println("subscribe onCompleted called");
    }
    
}
