package com.fbdl.grpc.benchmarkserver;

import com.fbdl.benchmark.grpc.Notification;
import com.fbdl.benchmark.grpc.SimRequest;
import com.fbdl.benchmark.grpc.SimResponse;
import com.fbdl.grpc.benchmarkserver.services.BmInternalService;
import com.fbdl.grpc.benchmarkserver.utils.BmProperties;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;

/**
 *
 * @author fbdl
 */
public class BmInternalServer implements Runnable {
    
    private final ConcurrentMap<String, StreamObserver<Notification>> subscribedHwMap;
    private final ConcurrentMap<String, SimRequest> simRequestMessageCache;
    private final ConcurrentMap<String, StreamObserver<SimResponse>> simResponseObserverMap;
    
    public BmInternalServer(ConcurrentMap subscribedHwMap, ConcurrentMap simRequestMessageCache, ConcurrentMap simResponseObserverMap) {
        this.subscribedHwMap = subscribedHwMap;
        this.simRequestMessageCache = simRequestMessageCache;
        this.simResponseObserverMap = simResponseObserverMap;
    }
    
    @Override
    public void run() {
        System.out.println("launching internal server");
        
        try {
            
            NettyServerBuilder.forPort(BmProperties.INSTANCE.getInternalPort())
                              .addService(new BmInternalService(subscribedHwMap, simRequestMessageCache, simResponseObserverMap))
                              .executor(Executors.newFixedThreadPool(BmProperties.INSTANCE.getEdgeThreads()))
                              .build()
                              .start()
                              .awaitTermination();
        }
        catch (InterruptedException | IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
