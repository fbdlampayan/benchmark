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
            long start = System.nanoTime();
            SimResponse response = clientBlocking.provisionSim(request);
            long end = System.nanoTime();
            long out = end - start;
            System.out.println("response: " + response.toString());
            System.out.println("ET: " + out / 1000000);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            channel.shutdown();
        }
    }
    
}
