package com.zjh;

import java.io.IOException;

/**
 * @PackageName: com.zjh
 * @ClassName:   ClientB
 * @Date:        2019/7/7 17:25
 *         
 * @Author: Jiahui Zou
 * @Description: TODO
 **/

public class ClientB {

    public static void  main(String[] args) throws IOException {
        NioClient nioClient = new NioClient();
        nioClient.start("CLientB");
    }
}
