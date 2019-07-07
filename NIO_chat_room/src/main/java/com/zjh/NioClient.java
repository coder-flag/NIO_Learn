package com.zjh;

import jdk.nashorn.internal.ir.WhileNode;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * @PackageName: com.zjh
 * @ClassName:   NioClient
 * @Date:        2019/7/2 21:36
 *         
 * @Author: Jiahui Zou
 * @Description: 聊天室客户端
 **/

public class NioClient {
    /**
    * 启动
    **/
    public void start() throws IOException {

        /**
        * 连接服务器端
        **/
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",8000));

        /**
         * 接受服务器端响应
         **/
        // 新开线程，专门负责接受服务器端的响应数据
        // selector , socketChannel ,注册
        Selector selector = Selector.open();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        new Thread(new NioClientHandler(selector)).start();


        /**
        * 服务器端发送数据
        **/
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()){
            String requst = scanner.nextLine();
            if(requst != null && requst.length() > 0){
                socketChannel.write(Charset.forName("UTF-8").encode(requst));
            }
        }


    }

    public static void  main(String[] args) throws IOException {
       NioClient nioClient = new NioClient();
       nioClient.start();
    }


}
