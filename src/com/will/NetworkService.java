package com.will;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Will on 2019/5/18.
 * code sample from https://docs.oracle.com/javase/8/docs/api/index.html?java/util/concurrent/ExecutorService.html
 */
public class NetworkService implements Runnable {
    private final ServerSocket serverSocket;
    private final ExecutorService pool;

    public NetworkService(int port, int poolSize)
            throws IOException {
        serverSocket = new ServerSocket(port);
        pool = Executors.newFixedThreadPool(poolSize);
    }

    public void run() { // run the service
        try {
            AtomicInteger n = new AtomicInteger(0);
            for (;;) {
                Handler task = new Handler(serverSocket.accept());
                System.out.println("accept query: "+n.addAndGet(1));
                pool.submit(task);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
