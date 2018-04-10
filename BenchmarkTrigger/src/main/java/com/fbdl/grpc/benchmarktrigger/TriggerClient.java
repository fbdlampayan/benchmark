package com.fbdl.grpc.benchmarktrigger;

import com.fbdl.benchmark.grpc.SimRequest;
import com.fbdl.benchmark.grpc.SimResponse;
import com.fbdl.benchmark.grpc.SmServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 *
 * @author fbdl
 */
public class TriggerClient {
    
    public static void main(String[] args) {
        
        System.out.println("client start");
        
        String ip = "ndac-ems";
        int port = 8081;
        
        //does tcp handshake start here?
        ManagedChannel channel = ManagedChannelBuilder.forAddress(ip, port).usePlaintext(true).build();
        SmServiceGrpc.SmServiceBlockingStub clientBlocking = SmServiceGrpc.newBlockingStub(channel);
        
        try {
            SimRequest request = SimRequest.newBuilder().setHwid("111").setName("name from client").setImsi("imsi from client").build();
            //or does tcp handshake start here?
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
