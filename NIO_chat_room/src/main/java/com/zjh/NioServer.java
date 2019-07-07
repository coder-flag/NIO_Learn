package com.zjh;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @PackageName: com.zjh
 * @ClassName:   NioServer
 * @Date:        2019/7/2 21:36
 *         
 * @Author: Jiahui Zou
 * @Description:  聊天室服务器端
 **/

public class NioServer {

    /**
    *
    * @DESC: 启动
    * @RETURN:
    **/
    public void start() throws IOException {
        /**
        * 1.创建selector
        **/
        Selector selector = Selector.open();

        /**
        * 2.通过ServerSocketChannel 创建Channel 通道
        **/
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        /**
        * 3.为channel通道绑定监听端口
        **/
        serverSocketChannel.bind(new InetSocketAddress(8000));

        /**
        * 4.设置channel为非阻塞模式
        **/
        serverSocketChannel.configureBlocking(false);

        /**
        * 5.将channel注册到selector上，监听连接事件
        **/
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务器启动成功");

        /**
        * 6.循环等待新接入的连接
        **/
        for(;;){
            /**
            * TODO 获取可用的channel数量
            **/
            int readyChannels = selector.select();

            /**
            * TODO 为什么要这样？？
            **/
            if(readyChannels == 0){
                continue;
            }

            /**
            * 获取channel集合
            **/
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                /**
                * selectionkey 实例
                **/
                SelectionKey selectionKey = (SelectionKey) iterator.next();
                /**
                * 移除Set中的当前selectionKey
                **/
                iterator.remove();


                /**
                 * 7.根据就绪状态状态，调用对应方法处理业务逻辑
                 **/

                /**
                *
                * 如果是 接入事件 todo
                **/
                if(selectionKey.isAcceptable()){
                    acceptHandler(serverSocketChannel, selector);
                }

                /**
                * 如果是 可读事件 todo
                **/
                if(selectionKey.isReadable()){
                    readHandler(selectionKey, selector);
                }

            }

        }

    }

    /**
    * 接入事件处理器
    **/
    private  void acceptHandler(ServerSocketChannel serverSocketChannel , Selector selector) throws IOException {
        /**
        * 如果是接入事件 ， 创建socketChannel
        **/
        SocketChannel socketChannel = serverSocketChannel.accept();
        /**
        * 将socketChannel 设置为非阻塞模式
        **/
        socketChannel.configureBlocking(false);
        /**
        * 将channel注册到selector上 ，监听可读事件
        **/
        socketChannel.register(selector, SelectionKey.OP_READ);
        /**
        * 恢复客户端信息
        **/
        socketChannel.write(Charset.forName("UTF-8").encode("你与聊天室的其他成员都不是朋友关系，请注意隐私安全"));
    }

    /**
    *  可读事件处理器
    **/
    private void readHandler(SelectionKey selectionKey , Selector selector) throws IOException {
        /**
        * 要从 selectionKey 获取到已经就绪的channel
        **/
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

        /**
        * 创建buffer
        **/
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        /**
        * 循环读取客户端的请求信息
        **/
        String request = "";
        while (socketChannel.read(byteBuffer) > 1){
            /**
            * 切换buffer为读模式
            **/
            byteBuffer.flip();

            /**
            * 读取buffer中的内容
            **/
            request += Charset.forName("UTF-8").decode(byteBuffer);
        }

        /**
        * 将channel 再次注册到selector上，监听它的可读事件
        **/
        socketChannel.register(selector, SelectionKey.OP_READ);


        /**
        * 将客户端发送的请求信息，广播给其他客户端
        **/
        if(request.length() > 0 ){
            broadCast(selector, socketChannel, request);
        }
    }

    private void broadCast(Selector selector , SocketChannel sourceChannel, String request){
        /**
        * 获取到所有已经连接的客户端的channel
        **/
        Set<SelectionKey> selectionKeySet = selector.keys();

        selectionKeySet.forEach(selectionKey -> {
            Channel targetChannel = selectionKey.channel();
            if(targetChannel instanceof SocketChannel && targetChannel != sourceChannel){

                //将消息通知广播到客户端
                try {
                    ((SocketChannel) targetChannel).write(Charset.forName("UTF-8").encode(request));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        /**
        * 循环向所有channel广播信息
        **/
    }

    public static void  main(String[] args) throws IOException {
         NioServer nioServer = new NioServer();
         nioServer.start();

    }
}
