package com.sjdddd.sojsandbox.template.java;

import com.sjdddd.sojsandbox.model.ExecuteCodeRequest;
import com.sjdddd.sojsandbox.model.ExecuteCodeResponse;
import com.sjdddd.sojsandbox.model.JudgeInfo;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class JavaCodeJudgeTest {

    public static void main(String[] args) {

        // 测试输入
        List<String> inputs = Arrays.asList("3 4");

        JavaCodeSandboxTemplate javaCodeJudge = new JavaCodeSandboxTemplate() {
        };

        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(inputs);

        executeCodeRequest.setCode("public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        int a = Integer.parseInt(args[0]);\n" +
                "        int b = Integer.parseInt(args[1]);\n" +
                "        System.out.println(\"结果：\" + (a + b));\n" +
                "    }\n" +
                "}");
        executeCodeRequest.setLanguage("java");

        // 调用runFile方法
        ExecuteCodeResponse executeCodeResponse = javaCodeJudge.executeCode(executeCodeRequest);

        // 打印结果
        JudgeInfo judgeInfo = executeCodeResponse.getJudgeInfo();
        System.out.println("Status: " + executeCodeResponse.getStatus());
        System.out.println("Message: " + executeCodeResponse.getMessage());
        System.out.println("Time: " + judgeInfo.getTime());
        System.out.println("Memory: " + judgeInfo.getMemory());
        System.out.println("Output: " + executeCodeResponse.getOutputList());


        // 打印每个测试用例的结果
//        for (ExecuteMessage result : results) {
//            System.out.println("Exit Value: " + result.getExitValue());
//            System.out.println("Output: " + result.getMessage());
//            System.out.println("Error: " + result.getErrorMessage());
//        }
    }
}

