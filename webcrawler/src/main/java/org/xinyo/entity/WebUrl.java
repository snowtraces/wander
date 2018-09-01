package org.xinyo.entity;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

public class WebUrl {
    private String hashCode;
    private String url;
    private String type;
    private int depth;

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public WebUrl(){

    }
    public WebUrl(String url, String type, int depth){
        this.url = url;
        this.type = type;
        this.depth = depth;
        this.hashCode = String.valueOf(Hashing.md5().hashString(url,Charsets.UTF_8));
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

    @Override
    public String toString() {
        return "WebUrl{" +
                "hashCode='" + hashCode + '\'' +
                ", url='" + url + '\'' +
                ", type='" + type + '\'' +
                ", depth=" + depth +
                '}';
    }
}