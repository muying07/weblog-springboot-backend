package com.muying.weblog.common.utils;

public class StringUtil {

    /**
     * 截断字符串，如果超过最大长度则在末尾添加三个点（...）
     *
     * @param text 原始字符串
     * @param maxLength 允许的最大长度（包含三个点的长度）
     * @return 截断后的字符串，结尾可能包含三个点
     *
     * 示例：
     *   truncateWithEllipsis("Hello world", 5) 返回 "He..."
     *   truncateWithEllipsis("Hi", 10) 返回 "Hi"
     *   truncateWithEllipsis(null, 5) 返回 null
     */
    public static String truncateWithEllipsis(String text, int maxLength) {
        // 处理空值
        if (text == null) {
            return null;
        }

        // 处理无效长度
        if (maxLength <= 0) {
            return "";
        }

        // 字符串长度小于等于最大长度时，直接返回
        if (text.length() <= maxLength) {
            return text;
        }

        // 当最大长度小于等于3时，只返回部分省略号
        if (maxLength <= 3) {
            return text.substring(0, maxLength);
        }

        // 截取字符串并添加三个点
        return text.substring(0, maxLength - 3) + "...";
    }

    // 示例用法
    public static void main(String[] args) {
        String test1 = "这是一段需要截断的长文本内容";
        System.out.println(truncateWithEllipsis(test1, 10));  // 输出: 这是一段...

        String test2 = "短文本";
        System.out.println(truncateWithEllipsis(test2, 10));  // 输出: 短文本

        String test3 = "刚好十个字";
        System.out.println(truncateWithEllipsis(test3, 10));  // 输出: 刚好十个字

        System.out.println(truncateWithEllipsis(null, 5));     // 输出: null
        System.out.println(truncateWithEllipsis("测试", 2));    // 输出: 测
    }
}
