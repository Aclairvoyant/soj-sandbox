package com.sjdddd.sojsandbox.template.c;


import com.sjdddd.sojsandbox.model.ExecuteCodeRequest;
import com.sjdddd.sojsandbox.model.ExecuteCodeResponse;
import org.springframework.stereotype.Component;

/**
 * C 原生代码沙箱实现（直接复用模板方法）
 * @author 沈佳栋
 */
@Component
public class CNativeCodeSandbox extends CCodeSandboxTemplate {

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        return super.executeCode(executeCodeRequest);
    }
}
