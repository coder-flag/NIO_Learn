package com.zjh;

import java.io.IOException;

/**
 * @PackageName: com.zjh
 * @ClassName:   ClientA
 * @Date:        2019/7/7 17:24
 *         
 * @Author: Jiahui Zou
 * @Description: TODO
 **/

public class ClientA {

    public static void  main(String[] args) throws IOException {
        NioClient nioClient  = new NioClient();
        nioClient.start("ClientA");
    }
}
