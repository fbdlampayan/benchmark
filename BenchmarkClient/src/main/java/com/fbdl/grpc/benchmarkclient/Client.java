package com.fbdl.grpc.benchmarkclient;

import com.fbdl.benchmark.grpc.BmServiceGrpc;
import com.fbdl.benchmark.grpc.ServiceRequest;
import com.fbdl.benchmark.grpc.ServiceResponse;
import com.fbdl.grpc.benchmarkclient.utils.BmProperties;
import com.fbdl.grpc.benchmarkclient.utils.BmUtil;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import java.io.File;
import javax.net.ssl.SSLException;
import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author FBDL
 */
public class Client {

    private static BmServiceGrpc.BmServiceBlockingStub bmBlocking;
    
    private static ManagedChannel channel;
    private static final Logger LOG = LoggerFactory.getLogger(Client.class);
    
    public void start(String[] args) throws SSLException {
        String propertiesFilePath = "config/bm.properties"; //args[0];
        
        try {
            BmUtil.loadProperties(propertiesFilePath);
            BmUtil.initializeProperties();
        } catch (ConfigurationException ex) {
            LOG.error("Failed to load config file " + ex.getMessage());
            return;
        }
       
        triggerPolling();
//        triggerLongLived();

        while (true) {}

    }

    private void triggerPolling() {
        for (int x = 0; x < BmProperties.INSTANCE.getClients(); x++) {
            Polling p = new Polling(Integer.toString(x));
            Thread t = new Thread(p);
            t.start();
        }
    }

    private void triggerLongLived() throws SSLException {
        for(int x = 0; x < BmProperties.INSTANCE.getClients(); x++) {
            LongLivedProcess l = new LongLivedProcess(Integer.toString(x));
            Thread t = new Thread(l);
            t.start();
        }
    }

    private void triggerSimpleService() throws SSLException {
        channel = NettyChannelBuilder.forAddress(BmProperties.INSTANCE.getTargetAddress(),
                BmProperties.INSTANCE.getEdgePort())
                .sslContext(GrpcSslContexts.forClient().trustManager(new File(BmProperties.INSTANCE.getEdgeCertPath())).build())
                .build();
        
        bmBlocking = BmServiceGrpc.newBlockingStub(channel);
        ServiceRequest req = ServiceRequest.newBuilder().setName("client name").build();
        
        try {
            ServiceResponse res = bmBlocking.simpleService(req);
        } catch (StatusRuntimeException ex) {
            ex.printStackTrace();
        }
    }
       
    public static void main(String[] args) throws SSLException {
        Client client = new Client();
        client.start(args);
    }
    
}