package org.xinyo.common;

import org.xinyo.entity.WebUrl;

import java.util.*;

public class Data {
    private Map<String, WebUrl> fullUrlMap = new HashMap<>();
    private List<WebUrl> newUrlList = new ArrayList<>();
    private HashSet<String> newSet = new HashSet<>();


    /**
     * 添加新链接
     * @param url
     * @param type
     */
    public synchronized void  addUrl(String url, String type) {
        WebUrl webUrl = new WebUrl(url, type);

        String hash = webUrl.getHashCode();

        fullUrlMap.put(hash, webUrl);
        boolean isnew = newSet.add(hash);
        if(isnew){
            newUrlList.add(webUrl);
        }

    }

    public synchronized WebUrl getUrl(){
        WebUrl webUrl = newUrlList.remove(0);
        if (webUrl != null) {
            newSet.remove(webUrl.getHashCode());
            return webUrl;
        }

        return null;
    }


}



