package com.sjdddd.sojsandbox.template.cpp;

import cn.hutool.dfa.FoundWord;
import cn.hutool.dfa.WordTree;
import com.sjdddd.sojsandbox.CodeSandBox;
import com.sjdddd.sojsandbox.model.*;
import com.sjdddd.sojsandbox.model.enums.JudgeInfoMessageEnum;
import com.sjdddd.sojsandbox.model.enums.QuestionSubmitStatusEnum;
import com.sjdddd.sojsandbox.template.CommonCodeSandboxTemplate;
import com.sjdddd.sojsandbox.utils.ProcessUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * C++ 代码沙箱模板方法的实现
 */
@Slf4j
public abstract class CppCodeSandboxTemplate extends CommonCodeSandboxTemplate implements CodeSandBox {

    private static final String GLOBAL_CODE_DIR_NAME = "tmpCodeCpp";
    private static final String GLOBAL_CPP_FILE_NAME = "Main.cpp";
    private static final long TIME_OUT = 15000L;

    // C++代码黑名单，仅供参考，实际情况下需要根据C++的特性进行定制
    private static final List<String> blackList = Arrays.asList(
            // 潜在的危险系统调用
            "system", "exec", "fork",
            // 文件操作
            "fstream", "ofstream", "ifstream",
            // 动态内存分配
            "malloc", "calloc", "realloc", "free",
            // 线程和进程
            "thread", "pthread_create",
            // 其他
            "remove", "rename"
    );

    private static final WordTree WORD_TREE;

    static {
        WORD_TREE = new WordTree();
        WORD_TREE.addWords(blackList);
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();
        log.info("当前代码使用语言：" + language);

        // 黑名单检测
        FoundWord foundWord = WORD_TREE.matchWord(code);
        if (foundWord != null) {
            log.info("包含禁止词：" + foundWord.getFoundWord());
            return new ExecuteCodeResponse(null, "包含禁止词：" + foundWord.getFoundWord(),
                    QuestionSubmitStatusEnum.FAIL.getValue(), new JudgeInfo(JudgeInfoMessageEnum.DANGEROUS_OPERATION.getValue(), null, null));
        }

        // 保存用户的代码为文件
        File userCodeFile = saveCodeToFile(code, GLOBAL_CODE_DIR_NAME, GLOBAL_CPP_FILE_NAME);

        // 执行代码，获取输出结果
        List<ExecuteMessage> executeMessageList = runFile(userCodeFile, inputList);

        // 整理输出结果
        ExecuteCodeResponse outputResponse = getOutputResponse(executeMessageList);

        // 清理文件
        boolean success = deleteFile(userCodeFile);
        if (!success) {
            log.error("Failed to delete file, userCodeFilePath = {}", userCodeFile.getAbsolutePath());
        }

        return outputResponse;
    }

    public List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList) {
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        String executableFilePath = userCodeFile.getAbsolutePath().replace(".cpp", "");

        // 编译C++代码
        String compileCommand = String.format("g++ -std=c++14 -o %s %s", executableFilePath, userCodeFile.getAbsolutePath());
        try {
            Process compileProcess = new ProcessBuilder("bash", "-c", compileCommand).start();
            int compileExitCode = compileProcess.waitFor();
            if (compileExitCode != 0) {
                // 编译失败，收集错误信息
                String errorMessage = readStream(compileProcess.getErrorStream());
                executeMessageList.add(ExecuteMessage.builder()
                        .exitValue(1)
                        .message(errorMessage)
                        .errorMessage(JudgeInfoMessageEnum.COMPILE_ERROR.getValue())
                        .build());
                return executeMessageList;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            executeMessageList.add(ExecuteMessage.builder()
                    .exitValue(1)
                    .message(e.getMessage())
                    .errorMessage(JudgeInfoMessageEnum.SYSTEM_ERROR.getValue())
                    .build());
            return executeMessageList;
        }

        // 对每个输入执行编译后的程序并收集结果
        for (String input : inputList) {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder(executableFilePath);
                processBuilder.redirectErrorStream(true); // 合并标准错误和标准输出
                Process runProcess = processBuilder.start();

                // 向程序输入数据
                if (input != null) {
                    runProcess.getOutputStream().write(input.getBytes());
                    runProcess.getOutputStream().flush();
                    runProcess.getOutputStream().close();
                }

                // 等待程序执行完成或超时
                boolean finished = runProcess.waitFor(TIME_OUT, TimeUnit.MILLISECONDS);
                if (!finished) {
                    // 超时处理
                    runProcess.destroy();
                    executeMessageList.add(ExecuteMessage.builder()
                            .exitValue(1)
                            .message("Time Limit Exceeded")
                            .errorMessage(JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED.getValue())
                            .build());
                    continue;
                }
                ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(runProcess, "运行");
                System.out.println("本次运行结果：" + executeMessage);
                if (executeMessage.getExitValue() != 0) {
                    executeMessage.setExitValue(1);
                    executeMessage.setMessage(JudgeInfoMessageEnum.RUNTIME_ERROR.getText());
                    executeMessage.setErrorMessage(JudgeInfoMessageEnum.RUNTIME_ERROR.getValue());
                }
                executeMessageList.add(executeMessage);

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                executeMessageList.add(ExecuteMessage.builder()
                        .exitValue(1)
                        .message(e.getMessage())
                        .errorMessage(JudgeInfoMessageEnum.SYSTEM_ERROR.getValue())
                        .build());
            }
        }

        return executeMessageList;
    }

    private String readStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        return output.toString().trim();
    }
}
