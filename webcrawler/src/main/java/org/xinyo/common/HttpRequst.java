package org.xinyo.common;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class HttpRequst {
    private int timeout = 3000;
    private int maxBytes = 1000000;

    private static class RequstHolder {
        private static final HttpRequst INSTANCE = new HttpRequst();
    }

    public static final HttpRequst getInstance() {
        return HttpRequst.RequstHolder.INSTANCE;
    }

    /**
     * 页面请求
     *
     * @param url
     * @return
     */
    public static String requst(String url) {
        try {
            return getInstance().fetchString(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 二进制文件请求
     *
     * @param url
     * @return
     */
    public static String requstBinary(String url) {
        try {
            getInstance().fetchBinary(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 下载网页页面内容
     * @param url
     * @return
     * @throws IOException
     */
    private String fetchString(String url) throws IOException {
        HttpURLConnection connection = createHttpConnection(url, timeout);
        InputStream is = getInputStream(connection);

        // TODO cookie System.out.println(connection.getHeaderField("Set-Cookie"));
        // 网页
        String streamEncoding;
        String contentType = connection.getContentType() != null ? connection.getContentType() : "";
        byte[] data = streamToData(is);
        boolean isHaveCharset = contentType.contains("charset=");
        if (isHaveCharset) {
            streamEncoding = contentType.replaceAll("^.*charset=(.*)]?$", "$1");
            streamEncoding = streamEncoding != null ? streamEncoding : detectEncoding(data);
        } else {
            streamEncoding = "utf-8";
        }
//        System.err.println("返回内容编码:" + streamEncoding);
        return data != null ? new String(data, streamEncoding) : "";
    }

    private void fetchBinary(String url)
            throws IOException {
        HttpURLConnection connection = createHttpConnection(url, timeout);
        InputStream is = getInputStream(connection);

        // 目标文件夹，不存在则创建
        String path = url.replaceAll(".+://(.+[^/]{1})(/)?$", "$1");
        String fileName = path.replaceAll("^.+/(.+)$", "$1");
        String[] paths = path.split("/");
        StringBuffer fullPath = new StringBuffer();
        for (int i = 0; i < paths.length; i++) {

            if (paths.length > (i + 1)) {
                fullPath.append(paths[i]);
                fullPath.append("\\\\");// 路径
                File file = new File(fullPath.toString());
                if (!file.exists()) {
                    file.mkdir();
                    System.out.println("创建目录为：" + fullPath.toString());
                }
            }
        }
        File file = new File(fullPath.toString() + fileName);

        BufferedInputStream bis = new BufferedInputStream(is);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file, false));

        int inByte;
        while ((inByte = bis.read()) != -1)
            bos.write(inByte);
        bis.close();
        bos.close();
    }

    /**
     * 配置连接属性
     *
     * @param url
     * @param timeout
     * @return
     * @throws IOException
     */
    private static HttpURLConnection createHttpConnection(String url, int timeout)
            throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection(Proxy.NO_PROXY);
        connection.setRequestProperty("User-Agent", Constant.USER_AGENT_CHROME);
        connection.setRequestProperty("Accept", Constant.ACCEPT);
        connection.setRequestProperty("Accept-Encoding", Constant.ACCEPT_ENCODING);
        connection.setRequestProperty("Accept-Language", Constant.ACCEPT_LANGUAGE);
        connection.setRequestProperty("Cookie", Constant.COOKIE);
        connection.setRequestProperty("Cache-Control", "max-age=0");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout << 1);
        connection.setInstanceFollowRedirects(true);
        return connection;
    }

    /**
     * 获取输入流
     *
     * @param connection
     * @return
     * @throws IOException
     */
    private InputStream getInputStream(HttpURLConnection connection) throws IOException {
        String encoding = connection.getContentEncoding();
        InputStream is;
        if ("gzip".equalsIgnoreCase(encoding)) {
            is = new GZIPInputStream(connection.getInputStream());
        } else if ("deflate".equalsIgnoreCase(encoding)) {
            is = new InflaterInputStream(connection.getInputStream(),
                    new Inflater(true));
        } else {
            is = connection.getInputStream();
        }
        return is;
    }

    /**
     * 估计编码
     *
     * @param data
     * @return
     */
    private String detectEncoding(byte[] data) {
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(data, 0, data.length);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        detector.reset();
        return encoding;
    }

    private byte[] streamToData(InputStream is) {
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(is, 2048);
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            int bytesRead = output.size();
            byte[] arr = new byte[2048];
            while (true) {
                if (bytesRead >= maxBytes) {
                    break;
                }
                int n = in.read(arr);
                if (n < 0)
                    break;
                bytesRead += n;
                output.write(arr, 0, n);
            }
            return output.toByteArray();
        } catch (SocketTimeoutException e) {
            return null;
        } catch (IOException e) {
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                }
            }
        }
    }


}
