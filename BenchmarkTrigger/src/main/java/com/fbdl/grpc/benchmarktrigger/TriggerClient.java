/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fbdl.grpc.benchmarktrigger;

import com.fbdl.benchmark.grpc.SimRequest;
import com.fbdl.benchmark.grpc.SimResponse;
import com.fbdl.benchmark.grpc.SmServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

/**
 *
 * @author fbdl
 */
public class TriggerClient {
    
    public static void main(String[] args) {
        
        System.out.println("client start");
        
        String ip = "ndac-ems";
        int port = 8081;
        
        ManagedChannel channel = ManagedChannelBuilder.forAddress(ip, port).usePlaintext(true).build();
        SmServiceGrpc.SmServiceBlockingStub clientBlocking = SmServiceGrpc.newBlockingStub(channel);
        
        try {
            SimRequest request = SimRequest.newBuilder().setName("name from client").setImsi("imsi from client").build();
            SimResponse response = clientBlocking.provisionSim(request);
            System.out.println("response: " + response.toString());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            channel.shutdown();
        }
    }
    
}
