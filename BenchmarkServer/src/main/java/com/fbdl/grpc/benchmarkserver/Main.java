package com.fbdl.grpc.benchmarkserver;

import com.fbdl.benchmark.grpc.Notification;
import com.fbdl.benchmark.grpc.SimRequest;
import com.fbdl.benchmark.grpc.SimResponse;
import com.fbdl.grpc.benchmarkserver.services.BmService;
import com.fbdl.grpc.benchmarkserver.services.LongLived;
import com.fbdl.grpc.benchmarkserver.services.PollingServices;
import com.fbdl.grpc.benchmarkserver.utils.BmProperties;
import com.fbdl.grpc.benchmarkserver.utils.BmUtil;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author FBDL
 */
public class Main {
    
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    
    private final ConcurrentMap<String, StreamObserver<Notification>> subscribedHwMap = new ConcurrentHashMap<>(16, 0.9f, 1);
    private final ConcurrentMap<String, SimRequest> simRequestMessageCache = new ConcurrentHashMap<>(16, 0.9f, 1);
    private final ConcurrentMap<String, StreamObserver<SimResponse>> simResponseObserverMap = new ConcurrentHashMap<>(16, 0.9f, 1);
    
    public void start(String[] args) throws IOException, InterruptedException {
        String propertiesFilePath = "config/bm.properties"; //args[0];
        
        try {
            BmUtil.loadProperties(propertiesFilePath);
            BmUtil.initializeProperties();
        } catch (ConfigurationException ex) {
            LOG.error("Failed to load config file " + ex.getMessage());;
            return;
        }
        
        initializeInternalServer();
        initializeEdgeServer();
    }

    private void initializeEdgeServer() throws IOException, InterruptedException {
        LOG.info("server launched");
        System.out.println("launching");
        
        int servingPort = BmProperties.INSTANCE.getEdgePort();
        String serverKey = BmProperties.INSTANCE.getEdgeCertKey();
        String serverCert = BmProperties.INSTANCE.getEdgeCertPath();
        
        NettyServerBuilder.forPort(servingPort)
                          .useTransportSecurity(new File(serverCert), new File(serverKey))
                          .addService(new BmService())
                          .addService(new LongLived(subscribedHwMap, simRequestMessageCache, simResponseObserverMap))
                          .addService(new PollingServices(subscribedHwMap, simRequestMessageCache, simResponseObserverMap))
                          .keepAliveTime(10, TimeUnit.SECONDS)
                          .keepAliveTimeout(10, TimeUnit.SECONDS)
                          .executor(Executors.newFixedThreadPool(BmProperties.INSTANCE.getEdgeThreads()))
                          .build()
                          .start()
                          .awaitTermination();
    }
    
    private void initializeInternalServer() {
        BmInternalServer internalServer = new BmInternalServer(subscribedHwMap, simRequestMessageCache, simResponseObserverMap);
        Thread t = new Thread(internalServer);
        t.start();
    }    
    
    public static void main(String[] args) throws IOException, InterruptedException {
        Main server = new Main();
        server.start(args);
    }
    
}