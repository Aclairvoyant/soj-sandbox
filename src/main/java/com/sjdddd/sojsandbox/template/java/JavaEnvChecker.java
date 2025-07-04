package com.sjdddd.sojsandbox.template.java;

import com.sjdddd.sojsandbox.template.EnvChecker;
import java.io.File;
import java.io.InputStream;

public class JavaEnvChecker implements EnvChecker {
    private static final String WIN_JAVAC_PATH = "C:/Program Files/Java/jdk-17/bin/javac.exe";
    private static final String LINUX_JAVAC_PATH = "/usr/bin/javac";
    private static final String MAC_JAVAC_PATH = "/opt/homebrew/bin/javac";

    @Override
    public boolean isInstalled() {
        try {
            Process process = Runtime.getRuntime().exec(getJavacVersionCmd());
            InputStream in = process.getInputStream();
            byte[] buf = new byte[128];
            int len = in.read(buf);
            String output = new String(buf, 0, len > 0 ? len : 0);
            return output.contains("javac");
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isChocoInstalled() {
        try {
            Process process = Runtime.getRuntime().exec("where choco");
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void install() {
        String os = System.getProperty("os.name").toLowerCase();
        try {
            System.out.println("[Java] 开始安装Java环境，当前系统：" + os);
            String cmd;
            if (os.contains("win")) {
                if (!isChocoInstalled()) {
                    System.err.println("[Java] 未检测到choco，请先手动安装chocolatey: https://chocolatey.org/install");
                    System.err.println("[Java] 或手动下载安装JDK 17: https://www.oracle.com/java/technologies/downloads/");
                    return;
                }
                cmd = "choco install openjdk17";
            } else if (os.contains("mac")) {
                cmd = "brew install openjdk@17";
            } else {
                cmd = "sudo apt-get update && sudo apt-get install -y openjdk-17-jdk";
            }
            System.out.println("[Java] 执行安装命令：" + cmd);
            Process process = Runtime.getRuntime().exec(cmd);
            int exitCode = process.waitFor();
            System.out.println("[Java] 安装完成，退出码：" + exitCode);
        } catch (Exception e) {
            System.err.println("[Java] 安装Java环境失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String getLanguageName() {
        return "java";
    }

    @Override
    public boolean checkEnvPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return new File(WIN_JAVAC_PATH).exists();
        } else if (os.contains("mac")) {
            return new File(MAC_JAVAC_PATH).exists() || new File(LINUX_JAVAC_PATH).exists();
        } else {
            return new File(LINUX_JAVAC_PATH).exists();
        }
    }

    private String getJavacVersionCmd() {
        return "javac -version";
    }
} 