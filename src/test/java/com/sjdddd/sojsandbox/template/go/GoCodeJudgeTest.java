package com.sjdddd.sojsandbox.template.go;

import com.sjdddd.sojsandbox.model.ExecuteCodeRequest;
import com.sjdddd.sojsandbox.model.ExecuteCodeResponse;
import com.sjdddd.sojsandbox.model.JudgeInfo;
import com.sjdddd.sojsandbox.template.cpp.CppCodeSandboxTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class GoCodeJudgeTest {

    public static void main(String[] args) {

        // 测试输入
        List<String> inputs = Arrays.asList("3 4", "10 20", "100 200");

        GoCodeSandboxTemplate goCodeSandboxTemplate = new GoCodeSandboxTemplate() {
        };

        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(inputs);
        executeCodeRequest.setCode("package main\n\nimport \"fmt\"\n\nfunc main() {\n    var a, b int\n    fmt.Scan(&a, &b)\n    fmt.Println(a + b)\n}");
        executeCodeRequest.setLanguage("go");

        // 调用runFile方法
        ExecuteCodeResponse executeCodeResponse = goCodeSandboxTemplate.executeCode(executeCodeRequest);

        // 打印结果
        JudgeInfo judgeInfo = executeCodeResponse.getJudgeInfo();
        System.out.println("Status: " + executeCodeResponse.getStatus());
        System.out.println("Message: " + executeCodeResponse.getMessage());
        System.out.println("Time: " + judgeInfo.getTime());
        System.out.println("Memory: " + judgeInfo.getMemory());
        System.out.println("Output: " + executeCodeResponse.getOutputList());

    }
}
