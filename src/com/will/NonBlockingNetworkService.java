package com.will;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Will on 2019/5/29.
 * TODO: selector 应该设置为static变量，代码结构需要调整，Selector应该只需要在单一线程中跑
 */
public class NonBlockingNetworkService implements Runnable{
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    // Allocate buffer
    private ByteBuffer byteBuffer = ByteBuffer.allocate(10);
    public NonBlockingNetworkService(int port) {
        try{
            serverSocketChannel = ServerSocketChannel.open();
            // 要配合selector使用，必须非阻塞模式
            // 因而 FileChannel 不能与Selector一起使用
            serverSocketChannel.configureBlocking(false);
            ServerSocket serverSocket = serverSocketChannel.socket();
            serverSocket.bind(new InetSocketAddress(port));
            // selector 初始化
            selector = Selector.open();
            // 将ServerSocketChannel注册到selector上
            // 注意，ServerSocketChannel类似ServerSocket，只有accept操作。read/write操作交由建连之后的socket完成
            // 所以以下register中interest操作，只能是OP_ACCEPT.如果换作别的，会抛异常非法参数
            SelectionKey key = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Going to listen on "+port);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void run() {
        while (true){
            try {
                // 该操作阻塞等待
                // 返回selector中获取就绪的channel数量
                int num = selector.select();
                // 返回准备好的key
                // SelectionKey 实际是channel、selector的集合，顺便体现channel中事件状态
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeySet.iterator();
                while (iterator.hasNext()){
                    SelectionKey selectionKey = iterator.next();
                    if (selectionKey.isAcceptable()){
                        // 如果是accept事件，说明有从客户端发起的连接
                        // 从selectionKey中获取channel
                        // 猜测：以下serverSocketChannel1应该== serverSocketChannel
                        ServerSocketChannel serverSocketChannel1 = (ServerSocketChannel)selectionKey.channel();
                        SocketChannel socketChannel = serverSocketChannel1.accept();
                        System.out.println("get Connection from "+socketChannel);

                        //注册新的channel给selector
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector,SelectionKey.OP_READ);
                    }else if(selectionKey.isReadable()){
                        // 来自channel的Readable事件，因为上一个if中注册了相关interest
                        SocketChannel sc = (SocketChannel) selectionKey.channel();

                        // 从SocketChannel 中读取数据
                        // 首先到Buffer，再导出来
                        int bytesEchoed = 0;
                        StringBuffer sb = new StringBuffer();
                        while (true) {
                            byteBuffer.clear();
                            int r = sc.read(byteBuffer);
                            if (r <= 0) {
                                break;
                            }
                            sb.append(new String(byteBuffer.array()));
                            // 将buffer反转，方便写入channel
                            byteBuffer.flip();
                            // 回应客户端
                            sc.write(byteBuffer);
                            bytesEchoed += r;
                        }
                        System.out.println(sb.toString());
                        // 主动关闭连接
                        sc.close();
                    }
                    // important
                    iterator.remove();
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        }
    }
}
