package org.xinyo.common;

import com.google.common.base.Strings;

import java.io.File;

public class CommonUtils {
    private static String PATH = null;

    /**
     * 获取外部文件地址
     * @param fileName
     * @return
     */
    public static String getFilePath(String fileName) {
        if (Strings.isNullOrEmpty(PATH)) {
            PATH = new File(CommonUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent() + File.separator;
        }
        return PATH + fileName;
    }
}
