package com.sjdddd.sojsandbox.template.c;

import cn.hutool.dfa.FoundWord;
import cn.hutool.dfa.WordTree;
import com.sjdddd.sojsandbox.CodeSandBox;
import com.sjdddd.sojsandbox.model.ExecuteCodeRequest;
import com.sjdddd.sojsandbox.model.ExecuteCodeResponse;
import com.sjdddd.sojsandbox.model.ExecuteMessage;
import com.sjdddd.sojsandbox.model.JudgeInfo;
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
 * C 代码沙箱模板方法的实现
 */
@Slf4j
public abstract class CCodeSandboxTemplate extends CommonCodeSandboxTemplate implements CodeSandBox {

    private static final String GLOBAL_CODE_DIR_NAME = "tmpCodeC";
    private static final String GLOBAL_C_FILE_NAME = "Main.c";
    private static final long TIME_OUT = 15000L;

    // C代码黑名单，仅供参考，实际情况下需要根据C的特性进行定制

    private static final List<String> blackListC = Arrays.asList(
            // 系统调用
            "system",
            // 动态内存管理
            "malloc", "calloc", "realloc", "free",
            // 文件操作
            "fopen", "fclose", "fwrite", "fread", "fseek",
            // 进程操作
            "fork", "exec",
            // 网络操作
            "socket",
            // 其他潜在危险函数
            "popen", "system", "unlink"
    );


    private static final WordTree WORD_TREE;

    static {
        WORD_TREE = new WordTree();
        WORD_TREE.addWords(blackListC);
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
        File userCodeFile = saveCodeToFile(code, GLOBAL_CODE_DIR_NAME, GLOBAL_C_FILE_NAME);

        // 2. 编译代码，得到 class 文件
        ExecuteMessage compileFileExecuteMessage = compileFile(userCodeFile);
        System.out.println("编译结果：" + compileFileExecuteMessage);
        if (compileFileExecuteMessage.getErrorMessage() != null)
        {
            // 返回编译错误信息
            return new ExecuteCodeResponse(null, compileFileExecuteMessage.getMessage(), QuestionSubmitStatusEnum.FAIL.getValue(), new JudgeInfo(compileFileExecuteMessage.getErrorMessage(), null, null));
        }

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

    /**
     * 编译代码
     *
     * @param userCodeFile
     * @return
     */
    public ExecuteMessage compileFile(File userCodeFile)
    {
        String executableFilePath = userCodeFile.getAbsolutePath().replace(".c", "");
        String compileCommand = String.format("gcc -fno-asm -Wall -lm -std=c11 -o %s %s", executableFilePath, userCodeFile.getAbsolutePath());
        try
        {
            Process compileProcess = Runtime.getRuntime().exec(compileCommand);
            ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(compileProcess, "编译");
            // 编译失败
            if (executeMessage.getExitValue() != 0)
            {
                executeMessage.setExitValue(1);
                executeMessage.setMessage(JudgeInfoMessageEnum.COMPILE_ERROR.getText());
                executeMessage.setErrorMessage(JudgeInfoMessageEnum.COMPILE_ERROR.getValue());
            }
            return executeMessage;
        }
        catch (Exception e)
        {
            // 未知错误
            ExecuteMessage executeMessage = new ExecuteMessage();
            executeMessage.setExitValue(1);
            executeMessage.setMessage(e.getMessage());
            executeMessage.setErrorMessage(JudgeInfoMessageEnum.SYSTEM_ERROR.getValue());
            return executeMessage;
        }
    }

    public List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList) {
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        String executableFilePath = userCodeFile.getAbsolutePath().replace(".c", ".exe");

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
