package com.sjdddd.sojsandbox.template.python3;

import com.sjdddd.sojsandbox.template.EnvChecker;
import java.io.File;
import java.io.InputStream;

public class Python3EnvChecker implements EnvChecker {
    private static final String WIN_PYTHON_PATH1 = "C:/Python39/python.exe";
    private static final String WIN_PYTHON_PATH2 = "C:/Python311/python.exe";
    private static final String LINUX_PYTHON_PATH = "/usr/bin/python3";
    private static final String MAC_PYTHON_PATH1 = "/usr/local/bin/python3";
    private static final String MAC_PYTHON_PATH2 = "/opt/homebrew/bin/python3";

    @Override
    public boolean isInstalled() {
        try {
            Process process = Runtime.getRuntime().exec(getPython3VersionCmd());
            InputStream in = process.getInputStream();
            byte[] buf = new byte[128];
            int len = in.read(buf);
            String output = new String(buf, 0, len > 0 ? len : 0);
            return output.contains("Python");
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
            System.out.println("[Python3] 开始安装Python3环境，当前系统：" + os);
            String cmd;
            if (os.contains("win")) {
                if (!isChocoInstalled()) {
                    System.err.println("[Python3] 未检测到choco，请先手动安装chocolatey: https://chocolatey.org/install");
                    System.err.println("[Python3] 或手动下载安装Python: https://www.python.org/downloads/");
                    return;
                }
                cmd = "choco install python";
            } else if (os.contains("mac")) {
                cmd = "brew install python3";
            } else {
                cmd = "sudo apt-get update && sudo apt-get install -y python3";
            }
            System.out.println("[Python3] 执行安装命令：" + cmd);
            Process process = Runtime.getRuntime().exec(cmd);
            int exitCode = process.waitFor();
            System.out.println("[Python3] 安装完成，退出码：" + exitCode);
        } catch (Exception e) {
            System.err.println("[Python3] 安装Python3环境失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String getLanguageName() {
        return "python3";
    }

    @Override
    public boolean checkEnvPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return new File(WIN_PYTHON_PATH1).exists() || new File(WIN_PYTHON_PATH2).exists();
        } else if (os.contains("mac")) {
            return new File(MAC_PYTHON_PATH1).exists() || new File(MAC_PYTHON_PATH2).exists();
        } else {
            return new File(LINUX_PYTHON_PATH).exists();
        }
    }

    private String getPython3VersionCmd() {
        return "python3 --version";
    }
} 