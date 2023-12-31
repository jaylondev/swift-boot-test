package io.github.jaylondev.swift.boot.test.utils;

/**
 * @author jaylon 2023/12/31 15:57
 */
public class StringUtils {

    /**
     * 将字符串的首位字母改成小写
     *
     * @param input 输入字符串
     * @return 处理后的字符串
     */
    public static String firstLetterToLowercase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        char firstChar = Character.toLowerCase(input.charAt(0));
        return firstChar + input.substring(1);
    }
}
