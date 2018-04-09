package com.fbdl.grpc.benchmarkserver;

import com.fbdl.grpc.benchmarkserver.services.BmInternalService;
import com.fbdl.grpc.benchmarkserver.utils.BmProperties;
import io.grpc.netty.NettyServerBuilder;
import java.io.IOException;
import java.util.concurrent.Executors;

/**
 *
 * @author fbdl
 */
public class BmInternalServer implements Runnable {
    
    @Override
    public void run() {
        System.out.println("launching internal server");
        
        try {
            
            NettyServerBuilder.forPort(BmProperties.INSTANCE.getInternalPort())
                              .addService(new BmInternalService())
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
