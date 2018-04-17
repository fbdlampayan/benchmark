/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fbdl.grpc.benchmarkclient;

import com.fbdl.benchmark.grpc.EdgeSimResponse;
import com.fbdl.benchmark.grpc.LongLivedServiceGrpc;
import com.fbdl.benchmark.grpc.Notification;
import com.fbdl.benchmark.grpc.SimRequest;
import com.fbdl.benchmark.grpc.SimResponse;
import com.fbdl.benchmark.grpc.SubscribeRequest;
import com.fbdl.benchmark.grpc.Type;
import com.fbdl.grpc.benchmarkclient.utils.BmProperties;
import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLException;

/**
 *
 * @author lampayan
 */
public class LongLivedProcess implements Runnable {
    
    private static StreamObserver<EdgeSimResponse> requestObserverToServer;
    private static String transactionId;
    private String hwid;
    
    public LongLivedProcess(String hwid) {
        this.hwid = hwid;
    }
    
    public void triggerSubscribe() throws SSLException {
        
        ManagedChannel channel = NettyChannelBuilder.forAddress(BmProperties.INSTANCE.getTargetAddress(),
                BmProperties.INSTANCE.getEdgePort())
                .sslContext(GrpcSslContexts.forClient().trustManager(new File(BmProperties.INSTANCE.getEdgeCertPath())).build())
                .build();
        
        LongLivedServiceGrpc.LongLivedServiceStub asyncClient = LongLivedServiceGrpc.newStub(channel);
  
        //subscribe first
        StreamObserver<SubscribeRequest> requestSubscribeObserver = asyncClient.subscribe(new StreamObserver<Notification>() {
            @Override
            public void onNext(Notification v) {
                transactionId = v.getTransactionId();
                try {
                    //create procedure stream on a different tunnel
                    ManagedChannel channel = NettyChannelBuilder.forAddress(BmProperties.INSTANCE.getTargetAddress(), BmProperties.INSTANCE.getEdgePort())
                                                                .sslContext(GrpcSslContexts.forClient().trustManager(new File(BmProperties.INSTANCE.getEdgeCertPath())).build())
                                                                .build();
                    
                    LongLivedServiceGrpc.LongLivedServiceStub procedureClient = LongLivedServiceGrpc.newStub(channel);
                    StreamObserver<EdgeSimResponse> requestObserver = procedureClient.edgeProvisionSim(new StreamObserver<SimRequest>() {
                        @Override
                        public void onNext(SimRequest v) {
                            //so consume and respond to server
                            SimResponse res = SimResponse.newBuilder().setName("response long live client").build();
                            EdgeSimResponse procedureInit = EdgeSimResponse.newBuilder().setTransactionId(transactionId).setType(Type.RESPONSE).setSimResponseMessage(res).build();
                            requestObserverToServer.onNext(procedureInit);
                            requestObserverToServer.onCompleted();
                            
                        }

                        @Override
                        public void onError(Throwable thrwbl) {
                            System.out.println("EdgeSimResponse onError " + thrwbl.getMessage());
                        }

                        @Override
                        public void onCompleted() {
                        }
                    });
                    
                    requestObserverToServer = requestObserver;
                    EdgeSimResponse procedureInit = EdgeSimResponse.newBuilder().setTransactionId(v.getTransactionId()).setType(Type.INIT).build();
                    requestObserverToServer.onNext(procedureInit);
                    
                } catch (SSLException ex) {
                    Logger.getLogger(LongLivedProcess.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }

            @Override
            public void onError(Throwable thrwbl) {
                System.out.println("requestSubscribeObserver onError " + thrwbl.getMessage());
            }

            @Override
            public void onCompleted() {
            }
        });
        
        SubscribeRequest subscribeRequest = SubscribeRequest.newBuilder().setHwid(hwid).build();
        requestSubscribeObserver.onNext(subscribeRequest);
        System.out.println("subscribed to server as hwid: " + hwid);
    }

    @Override
    public void run() {
        try {
            triggerSubscribe();
        } catch (SSLException ex) {
            Logger.getLogger(LongLivedProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
