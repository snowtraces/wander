package org.xinyo.common;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xinyo.entity.WebUrl;

import java.io.*;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.xinyo.common.Constant.*;

public class FileUtils {

    /**
     * 保存文件
     *
     * @param inputStream
     * @param webUrl
     */
    public static void save(WebUrl webUrl, InputStream inputStream) {
        if (inputStream == null) return;

        // 1. 文件目录初始化
        File file = initFile(webUrl);

        // 2. 判断文件类型
        try {
            String type = webUrl.getType();
            FileOutputStream outputStream = new FileOutputStream(file);
            if (URL_TYPE_TEXT.equals(type)) {
                // save
                // TODO 编码处理
                String html = CharStreams.toString(new InputStreamReader(inputStream, Config.getValue(DATA_CHAERSET_NAME)));

                Files.write(html, file, Charsets.UTF_8);

                // parse
                parseHtml(webUrl, html);

            } else {
                // save
                ByteStreams.copy(inputStream, outputStream);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化文件夹
     *
     * @param webUrl
     * @return
     */
    public static File initFile(WebUrl webUrl) {
        String url = webUrl.getUrl();
        url = url.substring(url.indexOf("//") + 2, url.length());
        String[] split = url.split("/");
        int length = split.length;

        String basePath = Config.getValue(DATA_BASE_PATH) + File.separator;
        String filePath = basePath;
        String fileName = "";

        // 创建文件夹
        for (int i = 0; i < length; i++) {
            if (i != length - 1) {
                filePath += split[i] + File.separator;
                File file = new File(filePath);
                if (!file.exists()) {
                    file.mkdir();
                }
            } else {
                fileName = split[i];
                if (validateFileName(fileName)) {
                    fileName = webUrl.getHashCode();
                }
            }
        }

        // 修改文件名
        if (!URL_TYPE_BINARY.equals(webUrl.getType())
                && !fileName.endsWith(".htm")
                && !fileName.endsWith(".html")) {
            fileName = fileName + ".htm";
        }
        filePath += fileName;
        return new File(filePath);
    }

    /**
     * 解析网页
     *
     * @param webUrl
     * @param html
     */
    public static void parseHtml(WebUrl webUrl, String html) {
        Document doc = Jsoup.parse(html, webUrl.getUrl());

        // 1. a 标签处理
        Elements aTags = doc.getElementsByTag("a");
        for (Element a : aTags) {
            String url = a.attr("href");
            // 过滤
            if (url == null || url.startsWith("#") || url.startsWith("javascript")) {
                continue;
            }

            // 添加链接
            if (url.endsWith(".pdf") || url.endsWith(".mp3")) {
                Data.addUrl(url, URL_TYPE_BINARY, webUrl.getDepth() + 1);
            } else {
                Data.addUrl(url, URL_TYPE_TEXT, webUrl.getDepth() + 1);
            }
        }

        // 2. img 标签处理
        Elements imgTags = doc.getElementsByTag("img");
        for (Element img : imgTags) {
            String url = img.attr("src");

            // 添加链接
            Data.addUrl(url, URL_TYPE_BINARY, webUrl.getDepth() + 1);
        }
    }


    private static boolean validateFileName(String fileName) {
        String regex = "[?*/\\<>:\"|]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(fileName);
        return m.find();
    }

    public static void main(String[] args) throws IOException {
//        WebUrl webUrl = new WebUrl();
////        webUrl.setType(URL_TYPE_BINARY);
//        webUrl.setUrl("https://xinyo.org");
////        InputStream inputStream = request(webUrl);
////        save(inputStream, webUrl);
//
//        String s = normalizeUrl(webUrl, "/archives/");
//        System.err.println(s);
        String fileName = "abccc";
        boolean b = validateFileName(fileName);
        System.out.println(b);

    }
}
