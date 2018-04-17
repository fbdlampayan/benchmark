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
        int numberOfRequests = 100;
        
        int numberOfClients = 1;
        
        doWarmUp(numberOfClients, ip, port, numberOfRequests);
        doBenchmark(ip, port, numberOfClients);
    }

    private static void doWarmUp(int numberOfClients, String ip, int port, int numberOfRequests) throws InterruptedException {
        System.out.println("================== warming up ==================");
        for(int x = 0; x < numberOfClients; x++) {
            ManagedChannel channel = ManagedChannelBuilder.forAddress(ip, port).usePlaintext(true).build();
            SmServiceGrpc.SmServiceBlockingStub clientBlocking = SmServiceGrpc.newBlockingStub(channel);
            try {
                SimRequest request = SimRequest.newBuilder().setHwid(Integer.toString(x)).setName("name from client").setImsi("imsi from client").build();
                for(int y = 0; y < numberOfRequests; y++) {
                    SimResponse response = clientBlocking.provisionSim(request);
                    System.out.println("response: " + response.getName());
//                    Thread.sleep(1000);
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            finally {
                channel.shutdown().awaitTermination(3, TimeUnit.SECONDS);
            }
        }
        System.out.println("================== warmup done! ==================");
    }

    private static void doBenchmark(String ip, int port, int numberOfClients) throws InterruptedException {
        int max = numberOfClients;
        long results[] = new long[max];

        System.out.println("================== benchmarking start ==================");
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
        System.out.println("================== benchmarking done! ==================");
        
        System.out.println("================== computing results ==================");
        long sum = 0;
        for(int a = 0; a < max; a++) {
            System.out.println("result [" + a + "]" + " : " + results[a]);
            sum = results[a] + sum;
        }
        
        System.out.println("sum: " + sum);
        System.out.println("average in ms: " + (sum/max)/1000000);
        System.out.println("=======================================================");
    }
    
}
