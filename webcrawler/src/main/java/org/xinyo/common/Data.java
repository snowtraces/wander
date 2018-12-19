package org.xinyo.common;

import com.google.common.base.Joiner;
import org.xinyo.entity.WebUrl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.xinyo.common.Constant.URL_TYPE_TEXT;

public class Data {
    private static volatile List<WebUrl> newTextList = new ArrayList<>();
    private static volatile List<WebUrl> newBinaryList = new ArrayList<>();
    private static String domain = "";

    /**
     * 初始化添加新链接
     */
    public static synchronized void initUrl(String url) {
        String domain = url.replaceAll("^.*://([^/]+).*$", "$1");// 域名
        String[] split = domain.split("\\.");
        int len = split.length;
        int start = len - 2;
        String root = split[start];
        while (root.matches("(org|com|net|edu|gov)")) {
            start = start--;
            root = split[start];
        }

        String[] newSplit = Arrays.copyOfRange(split, start, len);
        String rootDomain = Joiner.on(".").join(newSplit);
        Data.domain = rootDomain;

        addUrlForce(new WebUrl(url, URL_TYPE_TEXT, 0));
    }

    /**
     * 添加新链接
     * @param url
     * @param type
     */
    public static synchronized boolean addUrl(String url, String type, int depth) {
        // 判断是否跨域
        if(!url.contains(Data.domain)){
            return false;
        }

        // 判断过滤字段
        boolean addFilter = FilterUtils.addFilter(url);
        if (!addFilter) {
            return false;
        }

        WebUrl webUrl = new WebUrl(url, type, depth);
        String hash = webUrl.getHash();

        boolean isContain = BloomFilterUtils.check(hash);
        boolean isLogContain = BloomFilterUtils.checkLog(hash);

        if (isContain) {
            return false;
        }

        if (!isLogContain) {
            // 1. 日志不包含
            add(webUrl);
        } else if (type.equals(URL_TYPE_TEXT)) {
            // 2. 日志中包含，但类型为文本，进行读取后续链接
            add(webUrl);
        }
        return true;
    }

    public static synchronized WebUrl getUrl(){
        if (newTextList.size() == 0){
            if(newBinaryList.size() == 0) {
                return null;
            } else {
                return newBinaryList.remove(0);
            }
        } else {
            if (newBinaryList.size() == 0) {
                return newTextList.remove(0);
            } else {
                int second = LocalDateTime.now().getSecond();
                if ((second & 3) == 0) {
                    return newTextList.remove(0);
                } else {
                    return newBinaryList.remove(0);
                }
            }
        }
    }

    public static synchronized void addUrlForce(WebUrl webUrl){
        add(webUrl);
    }

    private static void add(WebUrl webUrl){
        String type = webUrl.getType();
        BloomFilterUtils.push(webUrl.getHash());
        if (type.equals(URL_TYPE_TEXT)) {
            newTextList.add(webUrl);
        } else {
            newBinaryList.add(webUrl);
        }
    }

}



