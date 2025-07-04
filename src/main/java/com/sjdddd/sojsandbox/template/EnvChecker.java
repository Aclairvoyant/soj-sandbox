package com.sjdddd.sojsandbox.template;

public interface EnvChecker {
    /**
     * 判断当前环境是否已安装
     */
    boolean isInstalled();

    /**
     * 自动安装当前环境（如有可能）
     */
    void install();

    /**
     * 获取语言名称
     */
    String getLanguageName();

    /**
     * 检查相关目录或可执行文件是否存在
     */
    boolean checkEnvPath();
} 