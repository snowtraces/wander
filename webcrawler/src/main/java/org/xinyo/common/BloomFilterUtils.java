package org.xinyo.common;

import com.google.common.base.Strings;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.util.List;

import static org.xinyo.common.Constant.BLOOM_FILTER_SIZE;

/**
 * 布隆过滤工具类
 */
public class BloomFilterUtils {
    private static BloomFilter bloomFilter = null;
    private static BloomFilter logBloomFilter = null;

    public static void initFilter() {
        // 1. 新建过滤器
        int filterSize = Config.getIntValue(BLOOM_FILTER_SIZE);
        bloomFilter = BloomFilter.create(Funnels.byteArrayFunnel(), filterSize, 0.001);
    }

    public static void initLogFilter() {
        // 1. 新建过滤器
        int filterSize = Config.getIntValue(BLOOM_FILTER_SIZE);
        List<String> strings = FileUtils.loadLog();
        int size = strings.size();
        filterSize = Math.max(filterSize, size * 2);

        logBloomFilter = BloomFilter.create(Funnels.byteArrayFunnel(), filterSize, 0.001);

         // 2. 初始化数据
        for (String s : strings) {
            if (Strings.isNullOrEmpty(s)) {
                continue;
            }
            logBloomFilter.put(s.getBytes());
        }

        System.err.println("logBloomFilter 初始化完毕");
    }

    public static void push(String input){
        bloomFilter.put(input.getBytes());
    }

    /**
     * 判断是否包含
     * @param input
     * @return true:已存在， false:不存在
     */
    public static boolean check(String input) {
        return bloomFilter.mightContain(input.getBytes());
    }

    public static boolean checkLog(String input) {
        return logBloomFilter.mightContain(input.getBytes());
    }
}
