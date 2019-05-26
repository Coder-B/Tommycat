package com.will.protocalBuilder;

import java.util.HashMap;

/**
 * Created by Will on 2019/5/26.
 */
public class HttpReqEntity {
    private String method;
    private String uri;
    private HashMap<String,String> headers = new HashMap<>();
    private String body;

    /**
     * @function 将TCP协议获取得到的client传递的数据按照HTTP协议parse
     * @param rawTcpData
     */
    public HttpReqEntity(String rawTcpData) {
        String[] reqArr = rawTcpData.split("\r\n\r\n");
        // 优先处理header
        String[] arr0 = reqArr[0].split("\r\n");
        fetchMethodAndUri(arr0[0],this);
        contructHeaders(arr0,this);
        // 如果包含body
        if (2 == reqArr.length){
            this.body = reqArr[1];
        }
    }

    private static void fetchMethodAndUri(String line, HttpReqEntity entity){
        String[] arr = line.split("\\s+");
        entity.method = arr[0];
        entity.uri = arr[1];
    }
    private static void contructHeaders(String[] arr, HttpReqEntity entity){
        for (int i = 1; i < arr.length; i++) {
            String[] innerArr = arr[i].split(":\\s+");
            entity.headers.put(innerArr[0],innerArr[1]);
        }
    }
}
