package org.xinyo.common;

import org.xinyo.entity.WebUrl;

import java.util.*;

import static org.xinyo.common.Constant.URL_TYPE_TEXT;

public class Data {
    private static List<WebUrl> newUrlList = new ArrayList<>();
    private static HashSet<String> newSet = new HashSet<>();
    private static String domain = "";

    /**
     * 初始化添加新链接
     */
    public static synchronized boolean initUrl(String url) {
        domain = url.replaceAll("^.*://([^/]+).*$", "$1");// 域名

        return addUrl(url, URL_TYPE_TEXT, 0);
    }

    /**
     * 添加新链接
     * @param url
     * @param type
     */
    public static synchronized boolean addUrl(String url, String type, int depth) {
        // 判断是否跨域
        String domain = url.replaceAll("^.*://([^/]+).*$", "$1");// 域名
        if(!Data.domain.equals(domain)){
            return false;
        }

        WebUrl webUrl = new WebUrl(url, type, depth);
        String hash = webUrl.getHashCode();

        boolean isnew = newSet.add(hash);
        if(isnew){
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



}



