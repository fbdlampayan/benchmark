package com.fbdl.grpc.benchmarkserver.services;

import com.fbdl.benchmark.grpc.BmServiceGrpc;
import com.fbdl.benchmark.grpc.ServiceRequest;
import com.fbdl.benchmark.grpc.ServiceResponse;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author FBDL
 */
public class BmService extends BmServiceGrpc.BmServiceImplBase {
    
    private static final Logger LOG = LoggerFactory.getLogger(BmService.class);
    
    public BmService() {}
    
    @Override
    public void simpleService(ServiceRequest request, StreamObserver<ServiceResponse> responseObserver) {
        LOG.info("simple Service invoked " + request.toString());
        System.out.println("simple Service invoked " + request.toString());
        
        ServiceResponse response = ServiceResponse.newBuilder()
                                                  .setName("name from server")
                                                  .build();
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}