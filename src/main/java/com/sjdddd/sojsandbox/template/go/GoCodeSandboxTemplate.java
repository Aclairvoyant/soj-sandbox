package com.sjdddd.sojsandbox.template.go;

import cn.hutool.dfa.FoundWord;
import cn.hutool.dfa.WordTree;
import com.sjdddd.sojsandbox.CodeSandBox;
import com.sjdddd.sojsandbox.model.*;
import com.sjdddd.sojsandbox.model.enums.JudgeInfoMessageEnum;
import com.sjdddd.sojsandbox.model.enums.QuestionSubmitStatusEnum;
import com.sjdddd.sojsandbox.template.CommonCodeSandboxTemplate;
import com.sjdddd.sojsandbox.utils.ProcessUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Go 代码沙箱模板方法的实现
 */
@Slf4j
public abstract class GoCodeSandboxTemplate extends CommonCodeSandboxTemplate implements CodeSandBox {

    private static final String GLOBAL_CODE_DIR_NAME = "tmpCodeGo";
    private static final String GLOBAL_GO_FILE_NAME = "Main.go";
    private static final long TIME_OUT = 15000L;

    // Go代码黑名单，仅供参考，实际情况下需要根据Go的特性进行定制
    private static final List<String> blackListGo = Arrays.asList(
        "os/exec",
        "net/http", "net",
        "io/ioutil", "os",
        "reflect"
    );

    private static final WordTree WORD_TREE;

    static {
        WORD_TREE = new WordTree();
        WORD_TREE.addWords(blackListGo);
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

        // Go的编译和执行逻辑
        File userCodeFile = saveCodeToFile(executeCodeRequest.getCode(), GLOBAL_CODE_DIR_NAME, GLOBAL_GO_FILE_NAME);
        ExecuteMessage compileMessage = compileFile(userCodeFile);
        if (compileMessage.getErrorMessage() != null) {
            return new ExecuteCodeResponse(null, compileMessage.getMessage(), QuestionSubmitStatusEnum.FAIL.getValue(), new JudgeInfo(compileMessage.getErrorMessage(), null, null));
        }

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

    public ExecuteMessage compileFile(File userCodeFile) {
        // Go编译逻辑
        String executableFilePath = userCodeFile.getAbsolutePath().replace(".go", "");
        String compileCommand = String.format("go build -o %s %s", executableFilePath, userCodeFile.getAbsolutePath());

        try {
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
        } catch (Exception e) {
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
        String executableFilePath = userCodeFile.getAbsolutePath().replace(".go", "");

//        String osName = System.getProperty("os.name").toLowerCase();
//        if (osName.contains("nix") || osName.contains("nux") || osName.contains("mac"))
//        {
//            executableFilePath = userCodeFile.getAbsolutePath().replace(".go", "");
//        }

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

}
