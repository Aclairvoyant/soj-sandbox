package com.sjdddd.sojsandbox.controller;

import com.sjdddd.sojsandbox.model.ExecuteCodeRequest;
import com.sjdddd.sojsandbox.model.ExecuteCodeResponse;
import com.sjdddd.sojsandbox.template.c.CNativeCodeSandbox;
import com.sjdddd.sojsandbox.template.cpp.CppCodeSandboxTemplate;
import com.sjdddd.sojsandbox.template.cpp.CppNativeCodeSandbox;
import com.sjdddd.sojsandbox.template.go.GoNativeCodeSandbox;
import com.sjdddd.sojsandbox.template.java.JavaCodeSandboxTemplate;
import com.sjdddd.sojsandbox.template.java.JavaNativeCodeSandbox;
import com.sjdddd.sojsandbox.template.python3.Python3CodeSandboxTemplate;
import com.sjdddd.sojsandbox.template.python3.Python3Native3CodeSandbox;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: 沈佳栋
 * @Description: TODO
 * @DateTime: 2024/3/7 21:46
 **/
@RestController("/")
public class MainController {

    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String AUTH_REQUEST_SECRET = "soj";

    @Resource
    private JavaNativeCodeSandbox javaNativeCodeSandbox;

    @Resource
    private Python3Native3CodeSandbox python3Native3CodeSandbox;

    @Resource
    private CppNativeCodeSandbox cppNativeCodeSandbox;

    @Resource
    private CNativeCodeSandbox cNativeCodeSandbox;

    @Resource
    private GoNativeCodeSandbox goNativeCodeSandbox;


    @GetMapping("/health")
    public String health() {
        return "ok";
    }


    @PostMapping("/executeCode")
    ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest executeCodeRequest, HttpServletRequest request,
                                    HttpServletResponse response) {

        String authHeader = request.getHeader(AUTH_REQUEST_HEADER);
        if (authHeader == null || !authHeader.equals(AUTH_REQUEST_SECRET)) {
            response.setStatus(403);
            throw new RuntimeException("权限不足");
        }

        if (executeCodeRequest == null) {
            throw new RuntimeException("参数错误");
        }
        String language = executeCodeRequest.getLanguage();
        if ("java".equals(language)) {
            return javaNativeCodeSandbox.executeCode(executeCodeRequest);
        } else if ("python".equals(language)) {
            return python3Native3CodeSandbox.executeCode(executeCodeRequest);
        } else if ("cpp".equals(language)) {
            return cppNativeCodeSandbox.executeCode(executeCodeRequest);
        } else if ("go".equals(language)) {
            return goNativeCodeSandbox.executeCode(executeCodeRequest);
        } else if ("c".equals(language)) {
            return cNativeCodeSandbox.executeCode(executeCodeRequest);
        } else {
            throw new RuntimeException("不支持的语言");
        }
    }
}
