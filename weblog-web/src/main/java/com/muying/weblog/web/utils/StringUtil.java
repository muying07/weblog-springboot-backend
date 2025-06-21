package com.muying.weblog.web.utils;

public class StringUtil {

    /**
     * 判断QQ号码是否合法
     * @param str
     * @return
     */
    public static boolean isPureNumber(String str) {
        return str.matches("[1-9][0-9]{4,14}");
    }
}
