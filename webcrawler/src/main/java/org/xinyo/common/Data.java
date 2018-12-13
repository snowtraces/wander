package org.xinyo.common;

import com.google.common.base.Joiner;
import org.xinyo.entity.WebUrl;

import java.util.*;

import static org.xinyo.common.Constant.URL_TYPE_TEXT;

public class Data {
    private static List<WebUrl> newUrlList = new ArrayList<>();
    private static String domain = "";

    /**
     * 初始化添加新链接
     */
    public static synchronized boolean initUrl(String url) {
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

        return addUrl(url, URL_TYPE_TEXT, 0);
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

        WebUrl webUrl = new WebUrl(url, type, depth);
        String hash = webUrl.getHashCode();

        boolean isContain = BloomFilterUtils.check(hash);

        if(!isContain){
            newUrlList.add(webUrl);
            return true;
        }
        return false;
    }

    public static synchronized WebUrl getUrl(){
        if(newUrlList.size() == 0){
            return null;
        }
        WebUrl webUrl = newUrlList.remove(0);
        return webUrl;
    }

    public static synchronized void addUrlForce(WebUrl webUrl){
        newUrlList.add(webUrl);
    }



}



