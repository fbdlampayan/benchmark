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
public class LongLivedProcess {
    
    private static StreamObserver<EdgeSimResponse> requestObserverToServer;
    private static String transactionId;
    
    public void triggerSubscribe() throws SSLException {
        System.out.println("trigger Subcribe started");
        
        ManagedChannel channel = NettyChannelBuilder.forAddress(BmProperties.INSTANCE.getTargetAddress(),
                BmProperties.INSTANCE.getEdgePort())
                .sslContext(GrpcSslContexts.forClient().trustManager(new File(BmProperties.INSTANCE.getEdgeCertPath())).build())
                .build();
        
        LongLivedServiceGrpc.LongLivedServiceStub asyncClient = LongLivedServiceGrpc.newStub(channel);
  
        //subscribe first
        StreamObserver<SubscribeRequest> requestSubscribeObserver = asyncClient.subscribe(new StreamObserver<Notification>() {
            @Override
            public void onNext(Notification v) {
                System.out.println("notification message received " + v.toString());
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
                            System.out.println("on next sim request from server " + v.toString());
                            //so consume and respond to server
                            SimResponse res = SimResponse.newBuilder().setName("response long live client").build();
                            EdgeSimResponse procedureInit = EdgeSimResponse.newBuilder().setTransactionId(transactionId).setType(Type.RESPONSE).setSimResponseMessage(res).build();
                            requestObserverToServer.onNext(procedureInit);
                            
                        }

                        @Override
                        public void onError(Throwable thrwbl) {
                            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        }

                        @Override
                        public void onCompleted() {
                            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onCompleted() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        
        System.out.println("subscribing to server");
        SubscribeRequest subscribeRequest = SubscribeRequest.newBuilder().setHwid("111").build();
        requestSubscribeObserver.onNext(subscribeRequest);
        System.out.println("subsribe sent");
            
        
    }
    
}
