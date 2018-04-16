/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fbdl.grpc.benchmarkserver.streams;

import com.fbdl.benchmark.grpc.EdgeSimResponse;
import com.fbdl.benchmark.grpc.Notification;
import com.fbdl.benchmark.grpc.SimRequest;
import com.fbdl.benchmark.grpc.SimResponse;
import io.grpc.stub.StreamObserver;
import com.fbdl.benchmark.grpc.Type;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author lampayan
 */
public class EdgeSimResponseStream implements StreamObserver<EdgeSimResponse> {
    
    private final StreamObserver<SimRequest> responseObserver;
    private final ConcurrentMap<String, SimRequest> simRequestMessageCache;
    private final ConcurrentMap<String, StreamObserver<SimResponse>> simResponseObserverMap;
    
    public EdgeSimResponseStream(ConcurrentMap simRequestMessageCache, ConcurrentMap simResponseObserverMap, StreamObserver<SimRequest> responseObserver) {
        this.simRequestMessageCache = simRequestMessageCache;
        this.simResponseObserverMap = simResponseObserverMap;
        this.responseObserver = responseObserver;
    }
    

    @Override
    public void onNext(EdgeSimResponse v) {
        if(v.getType() == Type.INIT) {
            SimRequest request = simRequestMessageCache.get(v.getTransactionId());
            responseObserver.onNext(request);
        }
        else {
            StreamObserver<SimResponse> streamToBack = simResponseObserverMap.get(v.getTransactionId());
            streamToBack.onNext(v.getSimResponseMessage());
            streamToBack.onCompleted();
        }
    }

    @Override
    public void onError(Throwable thrwbl) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onCompleted() {
        responseObserver.onCompleted();
    }
    
}
