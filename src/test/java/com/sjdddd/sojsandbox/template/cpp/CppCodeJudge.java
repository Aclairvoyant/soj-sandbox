package com.sjdddd.sojsandbox.template.cpp;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CppCodeJudge {

    // 编译和执行C++代码
    public static String compileAndRunCppCode(String code, List<String> inputs) {
        String sourceFilePath = "Main.cpp";
        String executableFilePath = "Main";

        // 保存C++代码到文件
        saveCodeToFile(code, sourceFilePath);

        // 编译C++代码
        String compileCommand = "g++ -o " + executableFilePath + " " + sourceFilePath;
        try {
            executeCommand(compileCommand, null);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error: Compilation failed.";
        }

        // 执行编译后的程序并捕获输出
        StringBuilder output = new StringBuilder();
        for (String input : inputs) {
            try {
                String runOutput = executeCommand("./" + executableFilePath, input);
                output.append(runOutput).append("\n");
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return "Error: Execution failed.";
            }
        }

        return output.toString();
    }

    // 保存代码到文件
    private static void saveCodeToFile(String code, String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(code);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 执行命令
    private static String executeCommand(String command, String input) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (input != null) {
            // 使用echo命令将输入重定向到执行的程序中
            List<String> commands = new ArrayList<>();
            commands.add("bash");
            commands.add("-c");
            commands.add(String.format("echo \"%s\" | %s", input, command));
            processBuilder.command(commands);
        } else {
            processBuilder.command("bash", "-c", command);
        }

        Process process = processBuilder.start();

        // 读取程序输出
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        int exitVal = process.waitFor();
        if (exitVal != 0) {
            // 处理错误输出或异常情况
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
            return "Error during the command execution";
        }

        return output.toString();
    }

    public static void main(String[] args) {
        // 测试C++代码
        String cppCode = "#include<iostream>\nusing namespace std;\nint main() {int a, b; cin >> a >> b; cout << (a+b) << endl; return 0;}";
        List<String> inputs = Arrays.asList("3 4", "5 6");
        String output = compileAndRunCppCode(cppCode, inputs);
        System.out.println("C++ Program Output:\n" + output);
    }
}
