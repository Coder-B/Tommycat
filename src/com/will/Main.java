package com.will;

import java.io.IOException;
import java.util.HashMap;

public class Main {
    private static final HashMap<String,String> APP_PARAM = new HashMap<>();
    private static final void processParam(String[] args){
        for (String pair: args){
            String[] arr = pair.split("=");
            if(2 == arr.length){
                APP_PARAM.put(arr[0],arr[1]);
            }
        }
    }

    /**
     * Req:  curl "http://localhost:8080/uri" -d 'hello world'
     * Req:  curl "http://localhost:8090/uri" -d 'hello world'
     * @param args
     */
    public static void main(String[] args) {
        processParam(args);
        try {
            if(!APP_PARAM.containsKey("IO") || "bio".equalsIgnoreCase(APP_PARAM.get("IO"))) {
                new Thread(new NetworkService(8080, 3)).start();
                new Thread(new NetworkService(8090, 3)).start();
            }else if("nio".equalsIgnoreCase(APP_PARAM.get("IO"))) {
                new Thread(new NonBlockingNetworkService(8090)).start();
            }
            System.out.println(Thread.currentThread().isDaemon());
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
