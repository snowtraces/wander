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

    private final static String COMMON_DOMAIN_SUFFIX =  "(org|com|net|edu|gov)";
    private final static Integer THREAD_NUMBER_RATIO = 3;

    /**
     * 初始化添加新链接
     */
    private static synchronized void initUrl(String url) {
        String domain = url.replaceAll("^.*://([^/]+).*$", "$1");
        String[] split = domain.split("\\.");
        int len = split.length;
        int start = len - 2;
        String root = split[start];
        while (COMMON_DOMAIN_SUFFIX.matches(root)) {
            root = split[--start];
        }

        String[] newSplit = Arrays.copyOfRange(split, start, len);
        Data.domain = String.join(".", newSplit);

        addUrlForce(new WebUrl(url, URL_TYPE_TEXT, 0));
    }
    public static void initUrlAndFilter(String url) {
        BloomFilterUtils.initFilter();
        BloomFilterUtils.initLogFilter();

        initUrl(url);
    }

    public static void initUrlAndFilter(List<String> urls) {
        BloomFilterUtils.initFilter();
        BloomFilterUtils.initLogFilter();

        urls.forEach(Data::initUrl);
    }


    /**
     * 添加新链接
     * @param url
     * @param type
     */
    static synchronized boolean addUrl(String url, String type, int depth) {
        // 判断是否跨域
        if(!url.contains(Data.domain)){
            return false;
        }

        if (depth > 0) {
            return false;
        }

        // 尾部过滤
        url = FilterUtils.removeTail(url);

        // 判断过滤字段
        boolean addFilter = FilterUtils.addFilter(url);
        if (!addFilter) {
            return false;
        }

        WebUrl webUrl = new WebUrl(url, type, depth);
        String hash = webUrl.getHash();

        boolean isContain = BloomFilterUtils.check(hash);

        if (isContain) {
            return false;
        }

        boolean isLogContain = BloomFilterUtils.checkLog(hash);
        if (!isLogContain) {
            // 1. 日志不包含
            add(webUrl);
        } else if (type.equals(URL_TYPE_TEXT)) {
            // 2. 日志中包含，但类型为文本，进行读取后续链接
            add(webUrl);
        }
        BloomFilterUtils.push(webUrl.getHash());
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
                if ((second & THREAD_NUMBER_RATIO) == 0) {
                    return newTextList.remove(0);
                } else {
                    return newBinaryList.remove(0);
                }
            }
        }
    }

    private static synchronized void addUrlForce(WebUrl webUrl){
        boolean isLogContain = BloomFilterUtils.checkLog(webUrl.getHash());
        if (!isLogContain) {
            add(webUrl);
        }
    }

    public static boolean isEmpty(){
        return newTextList.size() == 0 && newBinaryList.size() == 0;
    }

    private static void add(WebUrl webUrl){
        String type = webUrl.getType();
        if (type.equals(URL_TYPE_TEXT)) {
            newTextList.add(webUrl);
        } else {
            newBinaryList.add(webUrl);
        }
    }

}



