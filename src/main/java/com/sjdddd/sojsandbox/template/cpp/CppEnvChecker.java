package com.sjdddd.sojsandbox.template.cpp;

import com.sjdddd.sojsandbox.template.EnvChecker;
import java.io.File;
import java.io.InputStream;

public class CppEnvChecker implements EnvChecker {
    private static final String WIN_GPP_PATH = "C:/MinGW/bin/g++.exe";
    private static final String LINUX_GPP_PATH = "/usr/bin/g++";
    private static final String MAC_GPP_PATH = "/opt/homebrew/bin/g++";

    @Override
    public boolean isInstalled() {
        try {
            Process process = Runtime.getRuntime().exec(getGppVersionCmd());
            InputStream in = process.getInputStream();
            byte[] buf = new byte[128];
            int len = in.read(buf);
            String output = new String(buf, 0, len > 0 ? len : 0);
            return output.contains("g++");
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
            System.out.println("[C++] 开始安装C++语言环境，当前系统：" + os);
            String cmd;
            if (os.contains("win")) {
                if (!isChocoInstalled()) {
                    System.err.println("[C++] 未检测到choco，请先手动安装chocolatey: https://chocolatey.org/install");
                    System.err.println("[C++] 或手动下载安装MinGW: https://www.mingw-w64.org/downloads/");
                    return;
                }
                cmd = "choco install mingw";
            } else if (os.contains("mac")) {
                cmd = "brew install gcc";
            } else {
                cmd = "sudo apt-get update && sudo apt-get install -y g++";
            }
            System.out.println("[C++] 执行安装命令：" + cmd);
            Process process = Runtime.getRuntime().exec(cmd);
            int exitCode = process.waitFor();
            System.out.println("[C++] 安装完成，退出码：" + exitCode);
        } catch (Exception e) {
            System.err.println("[C++] 安装C++语言环境失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String getLanguageName() {
        return "cpp";
    }

    @Override
    public boolean checkEnvPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return new File(WIN_GPP_PATH).exists();
        } else if (os.contains("mac")) {
            return new File(MAC_GPP_PATH).exists() || new File(LINUX_GPP_PATH).exists();
        } else {
            return new File(LINUX_GPP_PATH).exists();
        }
    }

    private String getGppVersionCmd() {
        return "g++ --version";
    }
} 