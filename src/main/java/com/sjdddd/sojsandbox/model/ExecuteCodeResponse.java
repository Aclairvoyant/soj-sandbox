package com.sjdddd.sojsandbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: 沈佳栋
 * @Description: TODO
 * @DateTime: 2024/3/6 21:59
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeResponse {
    // 一组输出
    private List<String> outputList;

    // 接口信息
    private String message;

    // 判题状态
    private Integer status;

    private JudgeInfo judgeInfo;
}
