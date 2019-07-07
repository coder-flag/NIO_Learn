package com.zjh;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @PackageName: com.zjh
 * @ClassName:   NioClientHandler
 * @Date:        2019/7/7 16:55
 *         
 * @Author: Jiahui Zou
 * @Description:  客户端线程类 ，专门负责接受服务器的响应信息
 **/

public class NioClientHandler implements Runnable {
    private Selector selector;


    NioClientHandler(Selector selector){
        this.selector = selector;
    }

    @Override
    public void run()  {

        for(;;){
            /**
             * TODO 获取可用的channel数量
             **/
            int readyChannels = 0;
            try {
                readyChannels = selector.select();
            } catch (IOException e) {
                e.printStackTrace();
            }

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
                 * 如果是 可读事件 todo
                 **/
                if(selectionKey.isReadable()){
                    try {
                        readHandler(selectionKey, selector);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

        }
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
         * 循环读取服务器端的响应信息
         **/
        String response = "";
        while (socketChannel.read(byteBuffer) > 1){
            /**
             * 切换buffer为读模式
             **/
            byteBuffer.flip();

            /**
             * 读取buffer中的内容
             **/
            response += Charset.forName("UTF-8").decode(byteBuffer);
        }

        /**
         * 将channel 再次注册到selector上，监听它的可读事件
         **/
        socketChannel.register(selector, SelectionKey.OP_READ);


        /**
         * 将服务器端响应信息，打印到本地
         **/
        if(response.length() > 0 ){
            System.out.println("::" + response) ;
        }
    }
}
