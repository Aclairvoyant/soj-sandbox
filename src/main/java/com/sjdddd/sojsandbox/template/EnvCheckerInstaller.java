package com.sjdddd.sojsandbox.template;

import java.util.Arrays;
import java.util.List;

public class EnvCheckerInstaller {
    /**
     * 按顺序批量安装所有语言环境
     */
    public static void installAllInOrder() {
        List<String> order = Arrays.asList("c", "cpp", "go", "java", "python3");
        for (String lang : order) {
            EnvChecker checker = EnvCheckerRegistry.getChecker(lang);
            if (checker != null) {
                System.out.println("[Installer] 检查 " + lang + " 环境...");
                if (!checker.isInstalled()) {
                    System.out.println("[Installer] " + lang + " 未安装，开始安装...");
                    checker.install();
                } else {
                    System.out.println("[Installer] " + lang + " 已安装，无需重复安装。");
                }
            } else {
                System.out.println("[Installer] 未注册 " + lang + " 环境检测器，跳过。");
            }
        }
        System.out.println("[Installer] 所有环境安装流程结束。");
    }
} 