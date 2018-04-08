package com.fbdl.grpc.benchmarkserver;

import com.fbdl.grpc.benchmarkserver.services.BmService;
import com.fbdl.grpc.benchmarkserver.utils.BmProperties;
import com.fbdl.grpc.benchmarkserver.utils.BmUtil;
import io.grpc.netty.NettyServerBuilder;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author FBDL
 */
public class Main {
    
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    
    public void start(String[] args) throws IOException, InterruptedException {
        String propertiesFilePath = "config/bm.properties"; //args[0];
        
        try {
            BmUtil.loadProperties(propertiesFilePath);
            BmUtil.initializeProperties();
        } catch (ConfigurationException ex) {
            LOG.error("Failed to load config file " + ex.getMessage());;
            return;
        }
        
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
                          .executor(Executors.newFixedThreadPool(BmProperties.INSTANCE.getEdgeThreads()))
                          .build()
                          .start()
                          .awaitTermination();
    }
    
    public static void main(String[] args) throws IOException, InterruptedException {
        Main server = new Main();
        server.start(args);
    }
    
}