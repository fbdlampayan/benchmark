/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fbdl.grpc.benchmarkserver.streams;

import com.fbdl.benchmark.grpc.EdgeSimPollingResponse;
import com.fbdl.benchmark.grpc.PollingType;
import com.fbdl.benchmark.grpc.SimRequest;
import com.fbdl.benchmark.grpc.SimResponse;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author FBDL
 */
public class EdgeSimPollingResponseStream implements StreamObserver<EdgeSimPollingResponse>{

    private final ConcurrentMap<String, SimRequest> simRequestMessageCache;
    private final ConcurrentMap<String, StreamObserver<SimResponse>> simResponseObserverMap;
    private final StreamObserver<SimRequest> responseObserver;
    
    public EdgeSimPollingResponseStream(ConcurrentMap simRequestMessageCache, ConcurrentMap simResponseObserverMap, StreamObserver<SimRequest> responseObserver) {
        this.simRequestMessageCache = simRequestMessageCache;
        this.simResponseObserverMap = simResponseObserverMap;
        this.responseObserver = responseObserver;
    }

    @Override
    public void onNext(EdgeSimPollingResponse v) {
        if(v.getType() == PollingType.INIT) {
            SimRequest request = simRequestMessageCache.get(v.getTransactionId());
            simRequestMessageCache.remove(v.getTransactionId());
            responseObserver.onNext(request);
        }
        else {
            StreamObserver<SimResponse> streamToBack = simResponseObserverMap.get(v.getTransactionId());
            simResponseObserverMap.remove(v.getTransactionId());
            streamToBack.onNext(v.getSimResponseMessage());
            streamToBack.onCompleted();
        }
    }

    @Override
    public void onError(Throwable thrwbl) {
        System.out.println("EdgeSimPollingResponseStream onError");
    }

    @Override
    public void onCompleted() {
    }
    
}
