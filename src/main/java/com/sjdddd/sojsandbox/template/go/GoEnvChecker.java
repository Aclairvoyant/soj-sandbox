package com.sjdddd.sojsandbox.template.go;

import com.sjdddd.sojsandbox.template.EnvChecker;
import java.io.File;
import java.io.InputStream;

public class GoEnvChecker implements EnvChecker {
    private static final String WIN_GO_PATH = "C:/Go/bin/go.exe";
    private static final String LINUX_GO_PATH = "/usr/bin/go";
    private static final String MAC_GO_PATH1 = "/usr/local/go/bin/go";
    private static final String MAC_GO_PATH2 = "/opt/homebrew/bin/go";

    @Override
    public boolean isInstalled() {
        try {
            Process process = Runtime.getRuntime().exec(getGoVersionCmd());
            InputStream in = process.getInputStream();
            byte[] buf = new byte[128];
            int len = in.read(buf);
            String output = new String(buf, 0, len > 0 ? len : 0);
            return output.contains("go version");
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
            System.out.println("[Go] 开始安装Go语言环境，当前系统：" + os);
            String cmd;
            if (os.contains("win")) {
                if (!isChocoInstalled()) {
                    System.err.println("[Go] 未检测到choco，请先手动安装chocolatey: https://chocolatey.org/install");
                    System.err.println("[Go] 或手动下载安装Go: https://go.dev/dl/");
                    return;
                }
                cmd = "choco install golang";
            } else if (os.contains("mac")) {
                cmd = "brew install go";
            } else {
                cmd = "sudo apt-get update && sudo apt-get install -y golang";
            }
            System.out.println("[Go] 执行安装命令：" + cmd);
            Process process = Runtime.getRuntime().exec(cmd);
            int exitCode = process.waitFor();
            System.out.println("[Go] 安装完成，退出码：" + exitCode);
        } catch (Exception e) {
            System.err.println("[Go] 安装Go语言环境失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String getLanguageName() {
        return "go";
    }

    @Override
    public boolean checkEnvPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return new File(WIN_GO_PATH).exists();
        } else if (os.contains("mac")) {
            return new File(MAC_GO_PATH1).exists() || new File(MAC_GO_PATH2).exists();
        } else {
            return new File(LINUX_GO_PATH).exists();
        }
    }

    private String getGoVersionCmd() {
        return "go version";
    }
} 