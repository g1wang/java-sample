package com.stars.httpclient;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class HttpClinetUtils {
    /**
     * @param url
     * @param file
     * @return
     * @throws Exception
     */
    public JSONObject upload(String url, File file, String type) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        FileBody bin = new FileBody(file);
        // media_file 音视频文件
        // media_id 文件唯一标识符
        StringBody stringBody = new StringBody(type);
        HttpEntity reqEntity = null;
        HttpPost httppost = new HttpPost(url);
        try {
            reqEntity = MultipartEntityBuilder.create().addPart("images", bin).addPart("type", stringBody).build();
            httppost.setEntity(reqEntity);
            response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            JSONObject jsonReslut = new JSONObject();
            if (resEntity != null) {
                // 返回API结果
                jsonReslut = JSONObject.parseObject(EntityUtils.toString(resEntity));
            }
            EntityUtils.consume(resEntity);
            return jsonReslut;
        } catch (IOException e) {
            throw e;
        } finally {
            IOUtils.closeQuietly(response);
            IOUtils.closeQuietly(httpclient);
        }
    }


    /**
     *
     * @param url
     * @param files
     * @param i
     * @param startTime
     * @return
     * @throws Exception
     */
    public JSONObject imageUpload(String url, List<File> files, long i, long startTime) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            HttpEntity reqEntity = null;
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            for (File file : files) {
                FileBody bin = new FileBody(file);
                builder.addPart("images", bin);
            }
            reqEntity = builder.build();
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(reqEntity);
            response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(resEntity));
            EntityUtils.consume(resEntity);
            return jsonObject;
        } catch (IOException e) {
            throw e;
        } finally {
            IOUtils.closeQuietly(response);
            IOUtils.closeQuietly(httpclient);
        }
    }
}
