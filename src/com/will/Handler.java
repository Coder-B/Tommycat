package com.will;

import java.io.*;
import java.net.Socket;

/**
 * Created by Will on 2019/5/18.
 * code sample from https://docs.oracle.com/javase/8/docs/api/index.html?java/util/concurrent/ExecutorService.html
 */
public class Handler  implements Runnable {
    private final Socket socket;
    Handler(Socket socket) { this.socket = socket; }
    public void run() {
        // read and service request on socket
        try {
            System.out.println(Thread.currentThread().getId());
            InputStream inputStream = socket.getInputStream();
            Reader reader = new InputStreamReader(inputStream);
            char chars[] = new char[1024];
            StringBuilder builder = new StringBuilder();

            // If there are no bytes buffered on the socket, and the socket has not been closed using close,
            // then available will return 0.
            while (0 != inputStream.available()) {
                int len = reader.read(chars);
                builder.append(new String(chars, 0, len));
            }
//            System.out.println("Receive from client message=: \n" + builder);

            // Echo 服务器
            PrintStream os = new PrintStream(socket.getOutputStream());
            os.println(builder.toString());
            reader.close();
            socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
