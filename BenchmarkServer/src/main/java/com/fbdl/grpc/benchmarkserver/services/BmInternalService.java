/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fbdl.grpc.benchmarkserver.services;

import com.fbdl.benchmark.grpc.SimRequest;
import com.fbdl.benchmark.grpc.SimResponse;
import com.fbdl.benchmark.grpc.SmServiceGrpc;
import io.grpc.stub.StreamObserver;

/**
 *
 * @author fbdl
 */
public class BmInternalService extends SmServiceGrpc.SmServiceImplBase {
    
    @Override
    public void provisionSim(SimRequest request, StreamObserver<SimResponse> responseObserver) {
        System.out.println("provision sim invoked simple");
        
        SimResponse response = SimResponse.newBuilder()
                                          .setName("name from server")
                                          .setPnmid("id from server")
                                          .build();
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
    
}
