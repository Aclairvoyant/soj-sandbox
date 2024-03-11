package com.sjdddd.sojsandbox;

import com.sjdddd.sojsandbox.model.ExecuteCodeRequest;
import com.sjdddd.sojsandbox.model.ExecuteCodeResponse;

/**
 * @Author: 沈佳栋
 * @Description: 代码沙箱接口定义
 * @DateTime: 2024/3/6 21:51
 **/
public interface CodeSandBox {
    /**
     * 执行代码
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
