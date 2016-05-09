package com.ding.trans.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class Validation {

    public static void validateNotEmpty(String name, String value) {
        if (value == null || value.isEmpty()) {
            throw new RuntimeException(name + "是空的");
        }
    }

    public static void validateNotEmpty(String name, Map<?, ?> value) {
        if (value == null || value.isEmpty()) {
            throw new RuntimeException("没有" + name);
        }
    }

    public static void validateMaxSize(String name, String text, int limit) {
        if (text.length() > limit) {
            throw new RuntimeException(name + "字符数超过限制(<=" + limit + ")");
        }
    }

    public static void validateIntRange(String name, String text, int min, int max) {
        int n = -1;
        try {
            n = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new RuntimeException(name + "不是有效的整数");
        }
        if (n < min || n > max) {
            throw new RuntimeException(name + "需要在[" + min + ", " + max + "]范围内");
        }
    }

    public static void validateMatching(String name, String text, String regex) {
        if (!text.matches(regex)) {
            throw new RuntimeException(name + "格式不正确");
        }
    }

    public static void validateURL(String name, String url) {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(name + "URL格式不正确");
        }
    }

}
