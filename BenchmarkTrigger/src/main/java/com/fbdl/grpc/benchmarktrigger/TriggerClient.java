package com.fbdl.grpc.benchmarktrigger;

import com.fbdl.benchmark.grpc.SimRequest;
import com.fbdl.benchmark.grpc.SimResponse;
import com.fbdl.benchmark.grpc.SmServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author fbdl
 */
public class TriggerClient {
    
    public static void main(String[] args) throws InterruptedException {
        
        System.out.println("client start");
        
        String ip = "ndac-ems";
        int port = 8081;
        
        //does tcp handshake start here?
//        ManagedChannel channel = ManagedChannelBuilder.forAddress(ip, port).usePlaintext(true).build();
//        SmServiceGrpc.SmServiceBlockingStub clientBlocking = SmServiceGrpc.newBlockingStub(channel);
        
//        int y = Integer.parseInt(args[0]);
//        System.out.println("y: " + y);

        int max = 100;
        long results[] = new long[max];

        for(int x = 0; x < max; x++) {
            ManagedChannel channel = ManagedChannelBuilder.forAddress(ip, port).usePlaintext(true).build();
            SmServiceGrpc.SmServiceBlockingStub clientBlocking = SmServiceGrpc.newBlockingStub(channel);
            try {
                SimRequest request = SimRequest.newBuilder().setHwid(Integer.toString(x)).setName("name from client").setImsi("imsi from client").build();
                //or does tcp handshake start here?
                long start = System.nanoTime();
                SimResponse response = clientBlocking.provisionSim(request);
                long end = System.nanoTime();
                long out = end - start;
                results[x] = out;
                System.out.println("response: " + response.toString());
                System.out.println("ET in ms: " + out / 1000000);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            finally {
                channel.shutdown().awaitTermination(3, TimeUnit.SECONDS);
            }
        }
        
        long sum = 0;
        System.out.println("computing");
        for(int a = 0; a < max; a++) {
            System.out.println("result [" + a + "]" + " : " + results[a]);
            sum = results[a] + sum;
        }
        
        System.out.println("sum: " + sum);
        System.out.println("average in ms: " + (sum/max)/1000000);
    }
    
}
