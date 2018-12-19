package org.xinyo.common;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.xinyo.common.Constant.EXCLUDE_PATH;
import static org.xinyo.common.Constant.INCLUDE_PATH;

public class FilterUtils {

    public static boolean addFilter(String url) {
        List<String> exclude = getKeywordList(EXCLUDE_PATH);
        List<String> include = getKeywordList(INCLUDE_PATH);

        if (exclude.size() > 0) {
            for (String s : exclude) {
                if (url.contains(s)) {
                    return false;
                }
            }
        }

        if (include.size() > 0) {
            for (String s : include) {
                if (url.contains(s)) {
                    return true;
                }
            }
            return false;
        }

        return true;
    }

    private static List<String> getKeywordList(String name) {
        String exclude = Config.getValue(name);
        if (Strings.isNullOrEmpty(exclude)) {
            return new ArrayList<>();
        }

        String[] split = exclude.split(",");
        return Arrays.asList(split);
    }

}
