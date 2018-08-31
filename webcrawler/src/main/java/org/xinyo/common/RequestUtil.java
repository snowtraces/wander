package org.xinyo.common;

import com.google.common.io.ByteStreams;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.xinyo.entity.WebUrl;

import java.io.*;

import static org.xinyo.common.Constant.USER_AGENT_CHROME;

public class RequestUtil {

    /**
     * 请求
     * @param webUrl
     * @return
     */
    public static InputStream request(WebUrl webUrl) {
        CloseableHttpClient httpClient = getHttpClient();

        HttpGet httpget = new HttpGet(webUrl.getUrl());
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpget);

            HttpEntity entity = response.getEntity();
            System.out.println(response.getStatusLine());
            InputStream is = entity.getContent();
            return is;
        } catch (IOException e) {
            e.printStackTrace();
            if(response != null){
                try {
                    response.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            return null;
        }
    }

    private static class httpclientHolder {
        static CookieStore cookieStore = new BasicCookieStore();
        static RequestConfig config = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.DEFAULT)
                .setSocketTimeout(30000)
                .setConnectTimeout(30000)
                .setConnectionRequestTimeout(30000)
                .build();

        static CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .setDefaultRequestConfig(config)
                .setUserAgent(USER_AGENT_CHROME)
                .build();
        private static final CloseableHttpClient INSTANCE = httpClient;
    }

    public static final CloseableHttpClient getHttpClient() {
        return RequestUtil.httpclientHolder.INSTANCE;
    }


    public static void main(String[] args) throws IOException {
        WebUrl webUrl = new WebUrl();
        webUrl.setUrl("https://www.baeldung.com/wp-content/uploads/2018/08/RWS-Widget-Option2.jpg");
        InputStream inputStream = request(webUrl);

        File img = new File("img.jpg");
        FileOutputStream outputStream = new FileOutputStream(img);

        ByteStreams.copy(inputStream, outputStream);
    }

}
