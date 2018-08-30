package org.xinyo.entity;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

public class WebUrl {
    private String hashCode;
    private String url;
    private String type;

    public WebUrl(){

    }
    public WebUrl(String url, String type){
        this.url = url;
        this.type = type;
        this.hashCode = String.valueOf(Hashing.md5().hashString(type,Charsets.UTF_8));
    }

    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}