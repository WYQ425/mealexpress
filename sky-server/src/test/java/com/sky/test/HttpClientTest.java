package com.sky.test;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

/**
 * 测试HttpClient得两个请求
 */
@SpringBootTest
public class HttpClientTest {

    /**
     * 测试通过httpclient发送get请求
     */
    @Test
    public void testGET() throws Exception {
        //1、创建HttpClient对象，通过HttpClients.
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //2、创建请求对象httpGET,通过new对象指定url
        HttpGet httpGet = new HttpGet("http://localhost:8080/user/shop/status");

        //3、通过HttpClient对象得execute方法传进请求对象httpGET来发送请求，返回响应结果对象,异常抛出
        CloseableHttpResponse response = httpClient.execute(httpGet);

        //4、解析响应结果，获取响应头的状态码getStatusLine().getStatusCode()
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("响应的状态码为："+statusCode);
        //获取响应体getEntity,调用EntityUtils(依赖中的包)来转化json格式为string
        HttpEntity entity = response.getEntity();
        String body = EntityUtils.toString(entity);
        System.out.println("响应体数据为："+body);

        //关闭资源
        response.close();
        httpClient.close();
    }

    /**
     * 测试通过httpclient发送POST方式的请求
     */
    @Test
    public void testPOST() throws Exception{
        // 创建httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //创建请求对象
        HttpPost httpPost = new HttpPost("http://localhost:8080/admin/employee/login");

        //设置请求参数
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username","admin");
        jsonObject.put("password","123456");
        StringEntity entity = new StringEntity(jsonObject.toString());
        //指定请求编码方式
        entity.setContentEncoding("utf-8");
        //数据格式
        entity.setContentType("application/json");
        httpPost.setEntity(entity);

        //发送请求
        CloseableHttpResponse response = httpClient.execute(httpPost);

        //解析返回结果
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("响应码为：" + statusCode);

        HttpEntity entity1 = response.getEntity();
        String body = EntityUtils.toString(entity1);
        System.out.println("响应数据为：" + body);

        //关闭资源
        response.close();
        httpClient.close();
    }
}
