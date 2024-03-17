package com.sjdddd.sojsandbox.template.go;


import com.sjdddd.sojsandbox.model.ExecuteCodeRequest;
import com.sjdddd.sojsandbox.model.ExecuteCodeResponse;
import com.sjdddd.sojsandbox.template.cpp.CppCodeSandboxTemplate;
import org.springframework.stereotype.Component;

/**
 * Go 原生代码沙箱实现（直接复用模板方法）
 * @author 沈佳栋
 */
@Component
public class GoNativeCodeSandbox extends GoCodeSandboxTemplate {

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        return super.executeCode(executeCodeRequest);
    }
}
