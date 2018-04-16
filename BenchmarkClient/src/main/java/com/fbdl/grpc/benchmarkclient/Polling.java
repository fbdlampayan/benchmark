package com.fbdl.grpc.benchmarkclient;

import com.fbdl.benchmark.grpc.EdgeSimPollingResponse;
import com.fbdl.benchmark.grpc.PollingNotification;
import com.fbdl.benchmark.grpc.PollingRequest;
import com.fbdl.benchmark.grpc.PollingServiceGrpc;
import com.fbdl.benchmark.grpc.PollingType;
import com.fbdl.benchmark.grpc.SimRequest;
import com.fbdl.benchmark.grpc.SimResponse;
import com.fbdl.grpc.benchmarkclient.utils.BmProperties;
import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLException;

/**
 *
 * @author FBDL
 */
class Polling implements Runnable{
    
    private static StreamObserver<EdgeSimPollingResponse> requestObserverToServer;
    private static String transactionId;
    private String hwid;
    
    public Polling(String hwid) {
        this.hwid = hwid;
    }
    
    public void triggerPolling() throws SSLException {
        System.out.println("trigger Subcribe polling started " + hwid);
        
        while(true) {
            ManagedChannel channel = NettyChannelBuilder.forAddress(BmProperties.INSTANCE.getTargetAddress(), BmProperties.INSTANCE.getEdgePort())
                                                        .sslContext(GrpcSslContexts.forClient().trustManager(new File(BmProperties.INSTANCE.getEdgeCertPath())).build())
                                                        .build();
            
            PollingServiceGrpc.PollingServiceBlockingStub pollingClient = PollingServiceGrpc.newBlockingStub(channel);
            
            PollingRequest request = PollingRequest.newBuilder().setHwid(hwid).build();
            PollingNotification response = pollingClient.subscribe(request);
            
            if(response.getTransactionId().equalsIgnoreCase("none")) {
                //do nothing
            }
            else {
                transactionId = response.getTransactionId();
                    try {
                        //create procedure stream on a different tunnel
                        ManagedChannel procedureChannel = NettyChannelBuilder.forAddress(BmProperties.INSTANCE.getTargetAddress(), BmProperties.INSTANCE.getEdgePort())
                                                                    .sslContext(GrpcSslContexts.forClient().trustManager(new File(BmProperties.INSTANCE.getEdgeCertPath())).build())
                                                                    .build();

                        PollingServiceGrpc.PollingServiceStub procedureClient = PollingServiceGrpc.newStub(procedureChannel);
                        StreamObserver<EdgeSimPollingResponse> requestObserver = procedureClient.edgePollingProvisionSim(new StreamObserver<SimRequest>() {
                            @Override
                            public void onNext(SimRequest v) {
                                //so consume and respond to server
                                SimResponse res = SimResponse.newBuilder().setName("response polling client").build();
                                EdgeSimPollingResponse procedureResponse = EdgeSimPollingResponse.newBuilder().setTransactionId(transactionId).setType(PollingType.RESPONSE).setSimResponseMessage(res).build();
                                requestObserverToServer.onNext(procedureResponse);
                                requestObserverToServer.onCompleted();
                            }

                            @Override
                            public void onError(Throwable thrwbl) {
                                System.out.println("EdgeSimPollingResponse onError " + thrwbl);
                            }

                            @Override
                            public void onCompleted() {
                                procedureChannel.shutdown();
                            }
                        });

                        requestObserverToServer = requestObserver;
                        EdgeSimPollingResponse procedureInit = EdgeSimPollingResponse.newBuilder().setTransactionId(response.getTransactionId()).setType(PollingType.INIT).build();
                        requestObserverToServer.onNext(procedureInit);

                    } catch (SSLException ex) {
                        Logger.getLogger(LongLivedProcess.class.getName()).log(Level.SEVERE, null, ex);
                    }
            }
            pollingDelay(channel);
        }//end of while
            
        
    }

    private void pollingDelay(ManagedChannel channel) {
        channel.shutdown();
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(Polling.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try {
            triggerPolling();
        } catch (SSLException ex) {
            Logger.getLogger(Polling.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
