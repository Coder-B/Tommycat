package com.will;

import java.io.IOException;

public class Main {

    /**
     * Req:  curl "http://localhost:8080/uri" -d 'hello world'
     * Req:  curl "http://localhost:8090/uri" -d 'hello world'
     * @param args
     */
    public static void main(String[] args) {
        try {
            new Thread(new NetworkService(8080, 3)).start();
            new Thread(new NetworkService(8090, 3)).start();
            System.out.println(Thread.currentThread().isDaemon());
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
