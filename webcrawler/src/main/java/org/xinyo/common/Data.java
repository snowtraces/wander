package org.xinyo.common;

import org.xinyo.entity.WebUrl;

import java.util.*;

public class Data {
    private static Map<String, WebUrl> fullUrlMap = new HashMap<>();
    private static List<WebUrl> newUrlList = new ArrayList<>();
    private static HashSet<String> newSet = new HashSet<>();

    /**
     * 添加新链接
     * @param url
     * @param type
     */
    public static synchronized boolean addUrl(String url, String type, int depth) {
        WebUrl webUrl = new WebUrl(url, type, depth);
        String hash = webUrl.getHashCode();

        boolean isnew = newSet.add(hash);
        if(isnew){
            fullUrlMap.put(hash, webUrl);
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



