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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.xinyo.common.Constant.*;

public class RequestUtils {
    private final static CookieStore COOKIE_STORE = new BasicCookieStore();


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

            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity.getContent();
            } else {
                System.err.println("Unexpected response status: " + status);
                return  null;
            }

        } catch (IOException e) {
//            Data.addUrlForce(webUrl);
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

    private static CloseableHttpClient getHttpClient() {

         RequestConfig config = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.DEFAULT)
                .setSocketTimeout(30000)
                .setConnectTimeout(30000)
                .setConnectionRequestTimeout(30000)
                .build();

        return HttpClients.custom()
               .setDefaultCookieStore(COOKIE_STORE)
               .setDefaultRequestConfig(config)
               .setUserAgent(USER_AGENT_CHROME)
               .build();
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
