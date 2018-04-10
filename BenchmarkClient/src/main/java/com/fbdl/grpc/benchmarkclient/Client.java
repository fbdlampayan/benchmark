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
       
        
        Polling p = new Polling();
        p.triggerPolling();
        
//        triggerLongLived();
        System.out.println("done!!!");

        while (true) {}

    }

    private void triggerLongLived() throws SSLException {
        //triggerSimpleService();
        LongLivedProcess l = new LongLivedProcess();
        l.triggerSubscribe();
    }

    private void triggerSimpleService() throws SSLException {
        channel = NettyChannelBuilder.forAddress(BmProperties.INSTANCE.getTargetAddress(),
                BmProperties.INSTANCE.getEdgePort())
                .sslContext(GrpcSslContexts.forClient().trustManager(new File(BmProperties.INSTANCE.getEdgeCertPath())).build())
                .build();
        
        bmBlocking = BmServiceGrpc.newBlockingStub(channel);
        ServiceRequest req = ServiceRequest.newBuilder().setName("client name").build();
        
        try {
            System.out.println("calling");
            ServiceResponse res = bmBlocking.simpleService(req);
            LOG.info("message received " + res.toString());
            System.out.println("response " + res.toString());
        } catch (StatusRuntimeException ex) {
            ex.printStackTrace();
        }
    }
       
    public static void main(String[] args) throws SSLException {
        Client client = new Client();
        client.start(args);
    }
    
}