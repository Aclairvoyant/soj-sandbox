package com.sjdddd.sojsandbox.template;

import java.util.HashMap;
import java.util.Map;

public class EnvCheckerRegistry {
    
    private static final Map<String, EnvChecker> checkerMap = new HashMap<>();

    /**
     * 注册一个环境检测器
     */
    public static void registerChecker(EnvChecker checker) {
        checkerMap.put(checker.getLanguageName().toLowerCase(), checker);
    }

    /**
     * 获取指定语言的环境检测器
     */
    public static EnvChecker getChecker(String language) {
        return checkerMap.get(language.toLowerCase());
    }

    /**
     * 检查所有已注册语言环境
     */
    public static Map<String, Boolean> checkAllInstalled() {
        Map<String, Boolean> result = new HashMap<>();
        for (Map.Entry<String, EnvChecker> entry : checkerMap.entrySet()) {
            result.put(entry.getKey(), entry.getValue().isInstalled());
        }
        return result;
    }

    /**
     * 检查所有已注册语言的环境路径
     */
    public static Map<String, Boolean> checkAllEnvPath() {
        Map<String, Boolean> result = new HashMap<>();
        for (Map.Entry<String, EnvChecker> entry : checkerMap.entrySet()) {
            result.put(entry.getKey(), entry.getValue().checkEnvPath());
        }
        return result;
    }
} 