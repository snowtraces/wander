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
    public static synchronized void  addUrl(String url, String type) {
        WebUrl webUrl = new WebUrl(url, type);
        String hash = webUrl.getHashCode();

        boolean isnew = newSet.add(hash);
        if(isnew){
            fullUrlMap.put(hash, webUrl);
            newUrlList.add(webUrl);
        }
    }

    public static synchronized WebUrl getUrl(){
        WebUrl webUrl = newUrlList.remove(0);
        if (webUrl != null) {
            newSet.remove(webUrl.getHashCode());
            return webUrl;
        }

        return null;
    }


}



