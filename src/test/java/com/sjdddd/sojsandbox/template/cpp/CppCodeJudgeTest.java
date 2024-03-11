package com.sjdddd.sojsandbox.template.cpp;

import com.sjdddd.sojsandbox.model.ExecuteCodeRequest;
import com.sjdddd.sojsandbox.model.ExecuteCodeResponse;
import com.sjdddd.sojsandbox.model.ExecuteMessage;
import com.sjdddd.sojsandbox.model.JudgeInfo;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class CppCodeJudgeTest {

    public static void main(String[] args) {

        // 测试输入
        List<String> inputs = Arrays.asList("3 4", "10 20", "100 200");

        CppCodeSandboxTemplate cppCodeJudge = new CppCodeSandboxTemplate() {
        };

        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(inputs);
        executeCodeRequest.setCode("#include <iostream>\n" +
                "using namespace std;\n" +
                "int main() {\n" +
                "    int a, b;\n" +
                "    while (cin >> a >> b) {\n" +
                "        cout << a + b << endl;\n" +
                "    }\n" +
                "    return 0;\n" +
                "}");
        executeCodeRequest.setLanguage("cpp");

        // 调用runFile方法
        ExecuteCodeResponse executeCodeResponse = cppCodeJudge.executeCode(executeCodeRequest);

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
