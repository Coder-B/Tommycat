import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private static final int CHAR_SIZE = 1024;

    /**
     * Req:  curl "http://localhost:8080/uri" -d 'hello world'
     * @param args
     */
    public static void main(String[] args) {
        try {
            //完成bind和listen操作
            ServerSocket server = new ServerSocket(8080);
            Socket socket = server.accept();

            InputStream inputStream = socket.getInputStream();
            Reader reader = new InputStreamReader(inputStream);
            char chars[] = new char[CHAR_SIZE];
            StringBuilder builder = new StringBuilder();

            // If there are no bytes buffered on the socket, and the socket has not been closed using close,
            // then available will return 0.
            while(0 != inputStream.available()) {
                int len = reader.read(chars);
                builder.append(new String(chars, 0, len));
            }
            System.out.println("Receive from client message=: \n" + builder);

            // Echo 服务器
            PrintStream os = new PrintStream(socket.getOutputStream());
            os.println(builder.toString());
            reader.close();
            socket.close();
            server.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
